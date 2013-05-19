/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.rythmengine.internal;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.IRenderExceptionHandler;
import org.rythmengine.extension.IRythmListener;
import org.rythmengine.extension.ISourceCodeEnhancer;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.template.ITag;
import org.rythmengine.template.ITemplate;
import org.rythmengine.template.TemplateBase;
import org.rythmengine.utils.F;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatch {@link IEvent events}
 */
public class EventBus implements IEventDispatcher {
    protected static final ILogger logger = Logger.get(EventBus.class);
    private final RythmEngine engine;
    private final ISourceCodeEnhancer sourceCodeEnhancer;
    //private final IByteCodeEnhancer byteCodeEnhancer;
    private final IRenderExceptionHandler exceptionHandler;

    private static class RythmListenerDispatcher implements IRythmListener {
        private List<IRythmListener> listeners = new ArrayList<IRythmListener>();

        @Override
        public void onRender(ITemplate template) {
            for (IRythmListener l : listeners) {
                try {
                    l.onRender(template);
                } catch (RuntimeException e) {
                    logger.warn(e, "Error executing onRender method on rythm listener: " + l.getClass());
                }
            }
        }

        @Override
        public void rendered(ITemplate template) {
            for (IRythmListener l : listeners) {
                try {
                    l.rendered(template);
                } catch (RuntimeException e) {
                    logger.warn(e, "Error executing rendered method on rythm listener: " + l.getClass());
                }
            }
        }

        @Override
        public void enterInvokeTemplate(TemplateBase caller) {
            for (IRythmListener l : listeners) {
                try {
                    l.enterInvokeTemplate(caller);
                } catch (RuntimeException e) {
                    logger.warn(e, "Error executing enterInvokeTemplate method on rythm listener: " + l.getClass());
                }
            }
        }

        @Override
        public void exitInvokeTemplate(TemplateBase caller) {
            for (IRythmListener l : listeners) {
                try {
                    l.exitInvokeTemplate(caller);
                } catch (RuntimeException e) {
                    logger.warn(e, "Error executing exitInvokeTemplate method on rythm listener: " + l.getClass());
                }
            }
        }

        @Override
        public void onInvoke(ITag tag) {
            for (IRythmListener l : listeners) {
                try {
                    l.onInvoke(tag);
                } catch (RuntimeException e) {
                    logger.warn(e, "Error executing onInvoke method on rythm listener: " + l.getClass());
                }
            }
        }

        @Override
        public void invoked(ITag tag) {
            for (IRythmListener l : listeners) {
                try {
                    l.invoked(tag);
                } catch (RuntimeException e) {
                    logger.warn(e, "Error executing invoked method on rythm listener: " + l.getClass());
                }
            }
        }
    }

    private final RythmListenerDispatcher renderListener = new RythmListenerDispatcher();

    public final void registerRenderListener(IRythmListener l) {
        renderListener.listeners.add(l);
    }

    public final void unregisterRenderListener(IRythmListener l) {
        renderListener.listeners.remove(l);
    }

    public EventBus(RythmEngine engine) {
        this.engine = engine;
        RythmConfiguration conf = engine.conf();
        sourceCodeEnhancer = conf.get(RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER);
        //byteCodeEnhancer = conf.get(RythmConfigurationKey.CODEGEN_BYTE_CODE_ENHANCER);
        exceptionHandler = conf.get(RythmConfigurationKey.RENDER_EXCEPTION_HANDLER);
        IRythmListener l = conf.get(RythmConfigurationKey.RENDER_LISTENER);
        if (null != l) {
            registerRenderListener(l);
        }
        registerHandlers();
    }

    private static interface IEventHandler<RETURN, PARAM> {
        RETURN handleEvent(RythmEngine engine, PARAM param);
    }

    private Map<IEvent<?, ?>, IEventHandler<?, ?>> dispatcher = new HashMap<IEvent<?, ?>, IEventHandler<?, ?>>();

    @Override
    public Object accept(IEvent event, Object param) {
        IEventHandler handler = dispatcher.get(event);
        if (null != handler) {
            return handler.handleEvent(engine, param);
        }
        return null;
    }

    private void registerHandlers() {
        Map<IEvent<?, ?>, IEventHandler<?, ?>> m = dispatcher;
        m.put(RythmEvents.ON_PARSE, new IEventHandler<String, CodeBuilder>() {
            @Override
            public String handleEvent(RythmEngine engine, CodeBuilder c) {
                // pre process template source
                String tmpl = c.template();
                tmpl = tmpl.replaceAll("(\\r\\n)", "\n");
                tmpl = tmpl.replaceAll("\\r", "\n");
                return tmpl;
            }
        });
        m.put(RythmEvents.ON_BUILD_JAVA_SOURCE, new IEventHandler<Void, CodeBuilder>() {
            @Override
            public Void handleEvent(RythmEngine engine, CodeBuilder cb) {
                ISourceCodeEnhancer ce = sourceCodeEnhancer;
                if (null == ce) return null;
                if (cb.basicTemplate()) {
                    // basic template do not have common codes
                    return null;
                }
                // add common render args
                Map<String, ?> defArgs = ce.getRenderArgDescriptions();
                for (String name : defArgs.keySet()) {
                    Object o = defArgs.get(name);
                    String type = (o instanceof Class<?>) ? ((Class<?>) o).getName() : o.toString();
                    cb.addRenderArgs(-1, type, name);
                }
                // add common imports
                for (String s : ce.imports()) {
                    cb.addImport(s, -1);
                }
                return null;
            }
        });
        m.put(RythmEvents.ON_CLOSING_JAVA_CLASS, new IEventHandler<Void, CodeBuilder>() {
            @Override
            public Void handleEvent(RythmEngine engine, CodeBuilder cb) {
                // add common source code
                ISourceCodeEnhancer ce = sourceCodeEnhancer;
                if (null == ce) {
                    return null;
                }
                if (cb.basicTemplate()) {
                    // basic template do not have common codes
                    return null;
                }
                cb.np(ce.sourceCode());
                cb.pn();
                return null;
            }
        });
        m.put(RythmEvents.ON_RENDER, new IEventHandler<Void, ITemplate>() {
            @Override
            public Void handleEvent(RythmEngine engine, ITemplate template) {
                ISourceCodeEnhancer ce = engine.conf().get(RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER);
                if (null != ce) {
                    ce.setRenderArgs(template);
                }
                renderListener.onRender(template);
                return null;
            }
        });
        m.put(RythmEvents.RENDERED, new IEventHandler<Void, ITemplate>() {
            @Override
            public Void handleEvent(RythmEngine engine, ITemplate template) {
                engine.renderSettings.clear();
                Rythm.RenderTime.clear();
                renderListener.rendered(template);
                return null;
            }
        });
        m.put(RythmEvents.ON_TAG_INVOCATION, new IEventHandler<Void, F.T2<ITemplate, ITag>>() {
            @Override
            public Void handleEvent(RythmEngine engine, F.T2<ITemplate, ITag> param) {
                renderListener.onInvoke(param._2);
                return null;
            }
        });
        m.put(RythmEvents.TAG_INVOKED, new IEventHandler<Void, F.T2<TemplateBase, ITag>>() {
            @Override
            public Void handleEvent(RythmEngine engine, F.T2<TemplateBase, ITag> param) {
                renderListener.invoked(param._2);
                return null;
            }
        });
        m.put(RythmEvents.ENTER_INVOKE_TEMPLATE, new IEventHandler<Void, TemplateBase>() {
            @Override
            public Void handleEvent(RythmEngine engine, TemplateBase caller) {
                renderListener.enterInvokeTemplate(caller);
                return null;
            }
        });
        m.put(RythmEvents.EXIT_INVOKE_TEMPLATE, new IEventHandler<Void, TemplateBase>() {
            @Override
            public Void handleEvent(RythmEngine engine, TemplateBase caller) {
                renderListener.exitInvokeTemplate(caller);
                return null;
            }
        });
        m.put(RythmEvents.ON_RENDER_EXCEPTION, new IEventHandler<Boolean, F.T2<TemplateBase, Exception>>() {
            @Override
            public Boolean handleEvent(RythmEngine engine, F.T2<TemplateBase, Exception> param) {
                if (null == exceptionHandler) return false;
                return exceptionHandler.handleTemplateExecutionException(param._2, param._1);
            }
        });
    }
}
