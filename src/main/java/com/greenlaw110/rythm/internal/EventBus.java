package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.extension.IRenderExceptionHandler;
import com.greenlaw110.rythm.extension.ISourceCodeEnhancer;
import com.greenlaw110.rythm.extension.ITagInvokeListener;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.F;

import java.util.HashMap;
import java.util.Map;

/**
 * Dispatch {@link IEvent events}
 */
public class EventBus implements IEventDispatcher {
    private final RythmEngine engine;
    private final ISourceCodeEnhancer sourceCodeEnhancer;
    //private final IByteCodeEnhancer byteCodeEnhancer;
    private final IRenderExceptionHandler exceptionHandler;
    private final ITagInvokeListener tagInvokeListener;

    public EventBus(RythmEngine engine) {
        this.engine = engine;
        RythmConfiguration conf = engine.conf();
        sourceCodeEnhancer = conf.get(RythmConfigurationKey.CODEGEN_SOURCE_CODE_ENHANCER);
        //byteCodeEnhancer = conf.get(RythmConfigurationKey.CODEGEN_BYTE_CODE_ENHANCER);
        exceptionHandler = conf.get(RythmConfigurationKey.RENDER_EXCEPTION_HANDLER);
        tagInvokeListener = conf.get(RythmConfigurationKey.RENDER_TAG_INVOCATION_LISTENER);
        registerHandlers();
    }

    private static interface IEventHandler<PARAM> {
        void handleEvent(RythmEngine engine, PARAM param);
    }

    private Map<IEvent<?>, IEventHandler<?>> dispatcher = new HashMap<IEvent<?>, IEventHandler<?>>();

    @Override
    public void accept(IEvent event, Object param) {
        IEventHandler handler = dispatcher.get(event);
        if (null != handler) {
            handler.handleEvent(engine, param);
        }
    }

    private void registerHandlers() {
        Map<IEvent<?>, IEventHandler<?>> m = dispatcher;
        m.put(RythmEvents.ON_BUILD_JAVA_SOURCE, new IEventHandler<CodeBuilder>() {
            @Override
            public void handleEvent(RythmEngine engine, CodeBuilder cb) {
                ISourceCodeEnhancer ce = sourceCodeEnhancer;
                if (null == ce) return;
                if (cb.basicTemplate()) {
                    // basic template do not have common codes
                    return;
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
            }
        });
        m.put(RythmEvents.ON_CLOSING_JAVA_CLASS, new IEventHandler<CodeBuilder>() {
            @Override
            public void handleEvent(RythmEngine engine, CodeBuilder cb) {
                // add common source code
                ISourceCodeEnhancer ce = sourceCodeEnhancer;
                if (null == ce) return;
                if (cb.basicTemplate()) {
                    // basic template do not have common codes
                    return;
                }
                cb.np(ce.sourceCode());
                cb.pn();
            }
        });
        m.put(RythmEvents.ON_TAG_INVOCATION, new IEventHandler<F.T2<ITemplate, ITag>>() {
            @Override
            public void handleEvent(RythmEngine engine, F.T2<ITemplate, ITag> param) {
                ITagInvokeListener l = tagInvokeListener;
                if (null == l) {
                    return;
                }
                ITag tag = param._2;
                l.onInvoke(tag);
            }
        });
        m.put(RythmEvents.TAG_INVOKED, new IEventHandler<F.T2<TemplateBase, ITag>>() {
            @Override
            public void handleEvent(RythmEngine engine, F.T2<TemplateBase, ITag> param) {
                ITagInvokeListener l = tagInvokeListener;
                if (null == l) {
                    return;
                }
                ITag tag = param._2;
                l.invoked(tag);
            }
        });
        m.put(RythmEvents.ON_RENDER_EXCEPTION, new IEventHandler<F.T2<TemplateBase, Exception>>() {
            @Override
            public void handleEvent(RythmEngine engine, F.T2<TemplateBase, Exception> param) {
                exceptionHandler.handleTemplateExecutionException(param._2, param._1);
            }
        });
    }
}
