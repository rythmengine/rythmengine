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
package org.rythmengine;

import org.rythmengine.extension.ICodeType;
import org.rythmengine.template.ITemplate;
import org.rythmengine.toString.ToStringOption;
import org.rythmengine.toString.ToStringStyle;
import org.rythmengine.utils.Escape;

import java.io.File;
import java.util.Locale;
import java.util.Map;

/**
 * Rythm is a service wrapper of the {@link RythmEngine}.
 * <p/>
 * <p>For most cases you can play with <cod>Rythm</cod> instead of an individual
 * <code>RythmEngine</code> instance</p>
 * <p/>
 * <p>Since <code>Rythm</code> is a wrapper class, all services are provided via
 * static methods and delegated to an internal <code>RythmEngine</code> instance,
 * which as I called, the default engine</p>
 */
public class Rythm {
    
    public static boolean debug = false;

    /**
     * The type used to indicate the execution mode
     * of a {@link RythmEngine}
     */
    public static enum Mode {
        /**
         * Indicate the engine is run in a development mode
         */
        dev,
        /**
         * Indicate the engien is run in a product environment
         */
        prod;

        /**
         * Check if the current value is {@link #dev} or not
         *
         * @return true if the current value is {@link #dev} or false otherwise
         */
        public boolean isDev() {
            return dev == this;
        }

        /**
         * Check if the current value is {@link #prod} or not
         *
         * @return true if the current value is {@link #prod} or false otherwise
         */
        public boolean isProd() {
            return prod == this;
        }
    }

    // the default engine instance
    static RythmEngine engine = null;

    /**
     * Initialize default engine instance with specified configuration
     * <p/>
     * <p>Note this method can not be called more than once during a JVM lifecycle.
     * if the default engine instance is created already then
     * an <code>IllegalStateException</code> will be thrown out</p>
     * <p/>
     * <p>When the default engine's {@link RythmEngine#shutdown() shutdown} method get called
     * the default engine instance will be discard. Calling any servicing method of
     * <code>Rythm</code> will cause an new <code>RythmEngine</code> initialized as
     * the new default engine</p>
     *
     * @param conf the configurations
     */
    public static void init(Map<String, ?> conf) {
        if (null != engine) throw new IllegalStateException("Rythm is already initialized");
        engine = new RythmEngine(conf);
        // See #296
        ShutdownService service = getShutdownService(engine.conf().gae());
        service.setShutdown(new Runnable() {
            @Override
            public void run() {
              if (engine!=null)
                engine.shutdown();
            }
        });

        engine.setShutdownListener(new RythmEngine.IShutdownListener() {
            @Override
            public void onShutdown() {
                Rythm.engine = null;
            }
        });
    }

    public static ShutdownService getShutdownService(boolean isGaeAvailable) {
        if (!isGaeAvailable) {
            return DefaultShutdownService.INSTANCE;
        }

        try {
            String classname = "org.rythmengine.GaeShutdownService";
            Class clazz = Class.forName(classname);
            Object[] oa = clazz.getEnumConstants();
            ShutdownService result = (ShutdownService) oa[0];
            return result;
        } catch (Throwable t) {
            // Nothing to do
        }
        return DefaultShutdownService.INSTANCE;
    }

    /**
     * Initialize default engine instance with specified configuration file
     * <p/>
     * <p>Note this method can not be called more than once during a JVM lifecycle.
     * if the default engine instance is created already then
     * an <code>IllegalStateException</code> will be thrown out</p>
     * <p/>
     * <p>When the default engine's {@link RythmEngine#shutdown() shutdown} method get called
     * the default engine instance will be discard. Calling any servicing method of
     * <code>Rythm</code> will cause an new <code>RythmEngine</code> initialized as
     * the new default engine</p>
     *
     * @param file the configuration file
     */
    public static void init(File file) {
        if (null != engine) throw new IllegalStateException("Rythm is already initialized");
        engine = new RythmEngine(file);
        engine.setShutdownListener(new RythmEngine.IShutdownListener() {
            @Override
            public void onShutdown() {
                Rythm.engine = null;
            }
        });
    }

    /**
     * Initialize default engine instance with default configuration.
     * <p/>
     * <p>Just like {@link #init(java.util.Map)} method, this method cannot be called
     * more than once</p>
     * <p/>
     * <p>Note this default engine instance will be implicitly initialized with
     * the default configuration if <code>render</code> or the lazy load {@link #engine()}
     * methods are called before calling any <code>init</code> methods</p>
     */
    public static void init() {
        init((Map) null);
    }

    /**
     * Use an pre-initialized engine as the default engine. Note if there are already default
     * engine initialized then the method will raise a <code>IllegalStateException</code>
     * <p/>
     * <p>When the default engine's {@link RythmEngine#shutdown() shutdown} method get called
     * the default engine instance will be discard. Calling any servicing method of
     * <code>Rythm</code> will cause an new <code>RythmEngine</code> initialized as
     * the new default engine</p>
     *
     * @param engine
     */
    public static void init(RythmEngine engine) {
        if (null != Rythm.engine) throw new IllegalStateException("Rythm is already initialized");
        Rythm.engine = engine;
        engine.setShutdownListener(new RythmEngine.IShutdownListener() {
            @Override
            public void onShutdown() {
                Rythm.engine = null;
            }
        });
    }

    private static void checkInit() {
        if (null == engine) init();
    }

    /**
     * Return the default engine. If the default engine is not initialized yet, then initialize it and
     * then return
     *
     * @return default engine
     */
    public static RythmEngine engine() {
        checkInit();
        return engine;
    }

    /**
     * @return true if the current thread is in sandbox mode
     * @see org.rythmengine.RythmEngine#insideSandbox()
     */
    public static boolean insideSandbox() {
        return RythmEngine.insideSandbox();
    }

    /**
     * @return an new sandbox
     * @see org.rythmengine.RythmEngine#sandbox()
     */
    public static Sandbox sandbox() {
        return engine().sandbox();
    }

    /**
     * @param userContext
     * @return an new sandbox with user context specified
     * @see org.rythmengine.RythmEngine#sandbox(java.util.Map) 
     */
    public static Sandbox sandbox(Map<String, Object> userContext) {
        return engine().sandbox(userContext);
    }

    /**
     * Check if default engine is running in {@link Mode#prod production} mode
     *
     * @return true if default engine is in prod mode
     */
    public boolean isProdMode() {
        return engine().isProdMode();
    }

    /**
     * Prepare the render operation environment settings
     * 
     * @param codeType
     * @param locale
     * @param usrCtx
     * @return the engine instance itself
     */
    public final RythmEngine prepare(ICodeType codeType, Locale locale, Map<String, Object> usrCtx) {
        return engine().prepare(codeType, locale, usrCtx);
    }
    
    /**
     * Prepare the render operation environment settings
     * 
     * @param codeType
     * @return the engine instance itself
     */
    public final RythmEngine prepare(ICodeType codeType) {
        return engine().prepare(codeType);
    }

    /**
     * Prepare the render operation environment settings
     * 
     * @param locale
     * @return the engine instance itself
     */
    public final RythmEngine prepare(Locale locale) {
        return engine().prepare(locale);
    }
    
    /**
     * Prepare the render operation environment settings
     * 
     * @param userContext
     * @return the engine instance itself
     */
    public final RythmEngine prepare(Map<String, Object> userContext) {
        return engine().prepare(userContext);
    }

    /**
     * @param tmpl
     * @param args
     * @return
     */
    public static final ITemplate getTemplate(String tmpl, Object ... args) {
        return engine().getTemplate(tmpl, args);
    }

    public static final ITemplate getTemplate(File tmpl, Object ... args) {
        return engine().getTemplate(tmpl, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see RythmEngine#render(String, Object...)
     */
    public static String render(String template, Object... args) {
        return engine().render(template, args);
    }

    /**
     * @param file
     * @param args
     * @return render result
     * @see RythmEngine#render(java.io.File, Object...)
     */
    public static String render(File file, Object... args) {
        return engine().render(file, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see RythmEngine#substitute(String, Object...)
     */
    public static String substitute(String template, Object... args) {
        return engine().substitute(template, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see RythmEngine#substitute(java.io.File, Object...)
     */
    public static String substitute(File template, Object... args) {
        return engine().substitute(template, args);
    }

    /**
     * @param template
     * @param obj
     * @return render result
     * @see RythmEngine#toString(String, Object)
     */
    public static String toString(String template, Object obj) {
        return engine().toString(template, obj);
    }
    
    /**
     * @param obj
     * @return render result
     * @see RythmEngine#toString(Object)
     */
    public static String toString(Object obj) {
        return engine().toString(obj);
    }

    /**
     * @param obj
     * @param option
     * @param style
     * @return render result
     * @see RythmEngine#toString(Object, org.rythmengine.toString.ToStringOption, org.rythmengine.toString.ToStringStyle)
     */
    public static String toString(Object obj, ToStringOption option, ToStringStyle style) {
        return engine().toString(obj, option, style);
    }

    /**
     * @param obj
     * @param option
     * @param style
     * @return render result
     * @see RythmEngine#commonsToString(Object, org.rythmengine.toString.ToStringOption, org.apache.commons.lang3.builder.ToStringStyle)
     */
    public static String commonsToString(Object obj, ToStringOption option, org.apache.commons.lang3.builder.ToStringStyle style) {
        return engine().commonsToString(obj, option, style);
    }

    /**
     * Alias of {@link #renderString(String, Object...)}
     *
     * @param template
     * @param args
     * @return render result
     * @see RythmEngine#renderString(String, Object...)
     */
    public static String renderStr(String template, Object... args) {
        return engine().renderString(template, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see RythmEngine#renderString(String, Object...)
     */
    public static String renderString(String template, Object... args) {
        return engine().renderString(template, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see RythmEngine#renderIfTemplateExists(String, Object...)
     */
    public static String renderIfTemplateExists(String template, Object... args) {
        return engine().renderIfTemplateExists(template, args);
    }

    /**
     * @param s
     * @return the evaluation result
     */
    public static Object eval(String s) {
        return engine().eval(s);
    }

    /**
     * @see org.rythmengine.RythmEngine#shutdown()
     */
    public static void shutdown() {
        if (null == engine) return;
        engine.shutdown();
        engine = null;
    }
    
    public static final class RenderTime {
        private static final ThreadLocal<Escape> escape_ = new ThreadLocal<Escape>();
        public static void setEscape(Escape e) {
            escape_.set(e);
        }
        public static Escape getEscape() {
            Escape e = escape_.get();
            if (null == e) {
                ICodeType type = getCodeType();
                e = null == type ? Escape.XML : type.escape();
            }
            return e;
        }
        
        private static final ThreadLocal<ICodeType> type_ = new ThreadLocal<ICodeType>();
        public static void setCodeType(ICodeType type) {
            type_.set(type);
        }

        public static ICodeType getCodeType() {
            return type_.get();
        }
        
        public static void clear() {
            escape_.remove();
            type_.remove();
        }
    }

}
