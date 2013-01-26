package com.greenlaw110.rythm;

import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.logger.ILoggerFactory;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.toString.ToStringOption;
import com.greenlaw110.rythm.toString.ToStringStyle;
import com.greenlaw110.rythm.utils.IRythmListener;

import java.io.File;
import java.util.*;

/**
 * Rythm is a service wrapper of the {@link RythmEngine}.
 * 
 * <p>For most cases you can play with <cod>Rythm</cod> instead of an individual 
 * <code>RythmEngine</code> instance</p>
 * 
 * <p>Since <code>Rythm</code> is a wrapper class, all services are provided via
 * static methods and delegated to an internal <code>RythmEngine</code> instance,
 * which as I called, the default engine</p>
 */
public class Rythm {

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

    /**
     * A Type used to indicate the template reload approach used by the engine.
     * 
     * <p>Note this type is deprecated as {@link #V_VERSION is not used anymore}</p>
     */
    @Deprecated
    public static enum ReloadMethod {
        /**
         * Reload template class by attaching a version number to the new template class
         * name
         */
        V_VERSION,

        /**
         * Reload template class by restart engine and drop existing <code>ClassLoader</code>. The
         * new template class will be loaded by an new @{@link com.greenlaw110.rythm.internal.compiler.TemplateClassLoader}
         * instance
         */
        RESTART
    }

    // the default engine instance
    static RythmEngine engine = null;

    /**
     * Version of the default engine
     */
    public static final String version = engine.version;

    /**
     * Initialize default engine instance with specified properties
     * 
     * <p>Note this method can not be called more than once during a JVM lifecycle.
     * if the default engine instance is created already then
     * an <code>IllegalStateException</code> will be thrown out</p>
     * 
     * <p>When the default engine's {@link RythmEngine#shutdown() shutdown} method get called
     * the default engine instance will be discard. Calling any servicing method of 
     * <code>Rythm</code> will cause an new <code>RythmEngine</code> initialized as 
     * the new default engine</p>
     * 
     * @param conf the configuration properties
     */
    public static void init(Properties conf) {
        if (null != engine) throw new IllegalStateException("Rythm is already initialized");
        engine = new RythmEngine(conf);
        engine.setShutdownListener(new RythmEngine.IShutdownListener() {
            @Override
            public void onShutdown() {
                Rythm.engine = null;
            }
        });
    }

    /**
     * Initialize default engine instance with default configuration.
     * 
     * <p>Just like {@link #init(java.util.Properties)} method, this method cannot be called
     * more than once</p>
     * 
     * <p>Note this default engine instance will be implicitly initialized with  
     * the default configuration if <code>render</code> or the lazy load {@link #engine()}  
     * methods are called before calling any <code>init</code> methods</p>
     */
    public static void init() {
        init((Properties)null);
    }

    /**
     * Use an pre-initialized engine as the default engine. Note if there are already default
     * engine initialized then the method will raise a <code>IllegalStateException</code>
     * 
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
     * @return
     */
    public static RythmEngine engine() {
        checkInit();
        return engine;
    }

    /**
     * @see com.greenlaw110.rythm.RythmEngine#insideSandbox() 
     * 
     * @return
     */
    public static boolean insideSandbox() {
        return RythmEngine.insideSandbox();
    }

    /**
     * @see com.greenlaw110.rythm.RythmEngine#sandbox()  
     * 
     * @return
     */
    public static Sandbox sandbox() {
        return engine().sandbox();
    }

    /**
     * @see Logger#registerLoggerFactory(com.greenlaw110.rythm.logger.ILoggerFactory) 
     * 
     * @param fact
     */
    public static void registerLoggerFactory(ILoggerFactory fact) {
        Logger.registerLoggerFactory(fact);
    }

    /**
     * @see RythmEngine#registerListener(com.greenlaw110.rythm.utils.IRythmListener) 
     * 
     * @param listener
     */
    public static void registerListener(IRythmListener listener) {
        engine().registerListener(listener);
    }

    /**
     * @see RythmEngine#unregisterListener(com.greenlaw110.rythm.utils.IRythmListener) 
     * 
     * @param listener
     */
    public static void unregisterListener(IRythmListener listener) {
        engine().unregisterListener(listener);
    }

    /**
     * @see com.greenlaw110.rythm.RythmEngine#clearListener() 
     */
    public static void clearListener() {
        engine().clearListener();
    }

    /**
     * @see RythmEngine#registerTemplateClassEnhancer(com.greenlaw110.rythm.spi.ITemplateClassEnhancer) 
     * @param enhancer
     */
    public static void registerTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        engine().registerTemplateClassEnhancer(enhancer);
    }

    /**
     * @see RythmEngine#unregisterTemplateClassEnhancer(com.greenlaw110.rythm.spi.ITemplateClassEnhancer) 
     * @param enhancer
     */
    public static void unregisterTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        engine().unregisterTemplateClassEnhancer(enhancer);
    }

    /**
     * @see com.greenlaw110.rythm.RythmEngine#clearTemplateClassEnhancer() 
     */
    public static void clearTemplateClassEnhancer() {
        engine().clearTemplateClassEnhancer();
    }

    /**
     * @see RythmEngine#registerTag(com.greenlaw110.rythm.runtime.ITag) 
     * @param tag
     * @return
     */
    public static boolean registerTag(ITag tag) {
        return engine().registerTag(tag);
    }

    /**
     * @see RythmEngine#registerTag(String, com.greenlaw110.rythm.runtime.ITag)  
     * @param name
     * @param tag
     * @return
     */
    public boolean registerTag(String name, ITag tag) {
        return engine().registerTag(name, tag);
    }

    /**
     * Check if default engine is running in {@link Mode#prod production} mode
     * @return
     */
    public boolean isProdMode() {
        return engine().isProdMode();
    }

    /**
     * @see RythmEngine#render(String, Object...) 
     * @param template
     * @param args
     * @return
     */
    public static String render(String template, Object... args) {
        return engine().render(template, args);
    }

    /**
     * @see RythmEngine#render(java.io.File, Object...) 
     * @param file
     * @param args
     * @return
     */
    public static String render(File file, Object... args) {
        return engine().render(file, args);
    }

    /**
     * @see RythmEngine#substitute(String, Object...) 
     * @param template
     * @param args
     * @return
     */
    public static String substitute(String template, Object... args) {
        return engine().substitute(template, args);
    }

    /**
     * @see RythmEngine#substitute(java.io.File, Object...) 
     * @param template
     * @param args
     * @return
     */
    public static String substitute(File template, Object... args) {
        return engine().substitute(template, args);
    }

    /**
     * @see RythmEngine#toString(String, Object)  
     * @param template
     * @param obj
     * @return
     */
    public static String toString(String template, Object obj) {
        return engine().toString(template, obj);
    }

    /**
     * @see RythmEngine#toString(Object) 
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return engine().toString(obj);
    }

    /**
     * @see RythmEngine#toString(Object, com.greenlaw110.rythm.toString.ToStringOption, com.greenlaw110.rythm.toString.ToStringStyle) 
     * @param obj
     * @param option
     * @param style
     * @return
     */
    public static String toString(Object obj, ToStringOption option, ToStringStyle style) {
        return engine().toString(obj, option, style);
    }

    /**
     * @see RythmEngine#commonsToString(Object, com.greenlaw110.rythm.toString.ToStringOption, org.apache.commons.lang3.builder.ToStringStyle) 
     * @param obj
     * @param option
     * @param style
     * @return
     */
    public static String commonsToString(Object obj, ToStringOption option, org.apache.commons.lang3.builder.ToStringStyle style) {
        return engine().commonsToString(obj, option, style);
    }

    /**
     * Alias of {@link #renderString(String, Object...)}
     * 
     * @see RythmEngine#renderString(String, Object...) 
     * @param template
     * @param args
     * @return
     */
    public static String renderStr(String template, Object... args) {
        return engine().renderString(template, args);
    }

    /**
     * @see RythmEngine#renderString(String, Object...) 
     * @param template
     * @param args
     * @return
     */
    public static String renderString(String template, Object... args) {
        return engine().renderString(template, args);
    }

    /**
     * @see RythmEngine#renderIfTemplateExists(String, Object...) 
     * @param template
     * @param args
     * @return
     */
    public static String renderIfTemplateExists(String template, Object... args) {
        return engine().renderIfTemplateExists(template, args);
    }

    /**
     * @see com.greenlaw110.rythm.RythmEngine#shutdown() 
     */
    public static void shutdown() {
        engine().shutdown();
    }

    // --- SPI interfaces ---

    /**
     * @see com.greenlaw110.rythm.RythmEngine#getDialectManager() 
     * @return
     */
    public static DialectManager getDialectManager() {
        return engine().getDialectManager();
    }

    /**
     * @see com.greenlaw110.rythm.RythmEngine#getExtensionManager() 
     * @return
     */
    public static ExtensionManager getExtensionManager() {
        return engine().getExtensionManager();
    }
}
