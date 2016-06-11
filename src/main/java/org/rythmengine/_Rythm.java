/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine;

import org.rythmengine.toString.ToStringOption;
import org.rythmengine.toString.ToStringStyle;

import java.io.File;
import java.util.Map;

/**
 * _Rythm is clone of {@link Rythm} but for internal use of Rythm engine. User application
 * shall NOT use _Rythm
 */
public class _Rythm {

    // the default engine instance
    static RythmEngine engine = null;

    /**
     * Initialize default engine instance with specified configuration
     * <p/>
     * <p>Note this method can not be called more than once during a JVM lifecycle.
     * if the default engine instance is created already then
     * an <code>IllegalStateException</code> will be thrown out</p>
     * <p/>
     * <p>When the default engine's {@link org.rythmengine.RythmEngine#shutdown() shutdown} method get called
     * the default engine instance will be discard. Calling any servicing method of
     * <code>Rythm</code> will cause an new <code>RythmEngine</code> initialized as
     * the new default engine</p>
     *
     * @param conf the configurations
     */
    public static void init(Map<String, ?> conf) {
        if (null != engine) throw new IllegalStateException("Rythm is already initialized");
        engine = new RythmEngine(conf);
        engine.setShutdownListener(new RythmEngine.IShutdownListener() {
            @Override
            public void onShutdown() {
                _Rythm.engine = null;
            }
        });
    }
    
    /**
     * Initialize default engine instance with specified configuration file
     * <p/>
     * <p>Note this method can not be called more than once during a JVM lifecycle.
     * if the default engine instance is created already then
     * an <code>IllegalStateException</code> will be thrown out</p>
     * <p/>
     * <p>When the default engine's {@link org.rythmengine.RythmEngine#shutdown() shutdown} method get called
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
                _Rythm.engine = null;
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
     * <p>When the default engine's {@link org.rythmengine.RythmEngine#shutdown() shutdown} method get called
     * the default engine instance will be discard. Calling any servicing method of
     * <code>Rythm</code> will cause an new <code>RythmEngine</code> initialized as
     * the new default engine</p>
     *
     * @param engine
     */
    public static void init(RythmEngine engine) {
        if (null != _Rythm.engine) throw new IllegalStateException("Rythm is already initialized");
        _Rythm.engine = engine;
        engine.setShutdownListener(new RythmEngine.IShutdownListener() {
            @Override
            public void onShutdown() {
                _Rythm.engine = null;
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
     * @see RythmEngine#insideSandbox()
     */
    public static boolean insideSandbox() {
        return RythmEngine.insideSandbox();
    }

    /**
     * @return an new sandbox
     * @see RythmEngine#sandbox()
     */
    public static Sandbox sandbox() {
        return engine().sandbox();
    }

    /**
     * Check if default engine is running in {@link Rythm.Mode#prod production} mode
     *
     * @return true if default engine is in prod mode
     */
    public boolean isProdMode() {
        return engine().isProdMode();
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see org.rythmengine.RythmEngine#render(String, Object...)
     */
    public static String render(String template, Object... args) {
        return engine().render(template, args);
    }

    /**
     * @param file
     * @param args
     * @return render result
     * @see org.rythmengine.RythmEngine#render(java.io.File, Object...)
     */
    public static String render(File file, Object... args) {
        return engine().render(file, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see org.rythmengine.RythmEngine#substitute(String, Object...)
     */
    public static String substitute(String template, Object... args) {
        return engine().substitute(template, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see org.rythmengine.RythmEngine#substitute(java.io.File, Object...)
     */
    public static String substitute(File template, Object... args) {
        return engine().substitute(template, args);
    }

    /**
     * @param template
     * @param obj
     * @return render result
     * @see org.rythmengine.RythmEngine#toString(String, Object)
     */
    public static String toString(String template, Object obj) {
        return engine().toString(template, obj);
    }
    
    /**
     * @param obj
     * @return render result
     * @see org.rythmengine.RythmEngine#toString(Object)
     */
    public static String toString(Object obj) {
        return engine().toString(obj);
    }

    /**
     * @param obj
     * @param option
     * @param style
     * @return render result
     * @see org.rythmengine.RythmEngine#toString(Object, org.rythmengine.toString.ToStringOption, org.rythmengine.toString.ToStringStyle)
     */
    public static String toString(Object obj, ToStringOption option, ToStringStyle style) {
        return engine().toString(obj, option, style);
    }

    /**
     * @param obj
     * @param option
     * @param style
     * @return render result
     * @see org.rythmengine.RythmEngine#commonsToString(Object, org.rythmengine.toString.ToStringOption, org.apache.commons.lang3.builder.ToStringStyle)
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
     * @see org.rythmengine.RythmEngine#renderString(String, Object...)
     */
    public static String renderStr(String template, Object... args) {
        return engine().renderString(template, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see org.rythmengine.RythmEngine#renderString(String, Object...)
     */
    public static String renderString(String template, Object... args) {
        return engine().renderString(template, args);
    }

    /**
     * @param template
     * @param args
     * @return render result
     * @see org.rythmengine.RythmEngine#renderIfTemplateExists(String, Object...)
     */
    public static String renderIfTemplateExists(String template, Object... args) {
        return engine().renderIfTemplateExists(template, args);
    }

    /**
     * @param s
     * @return evaluation result
     */
    public static Object eval(String s) {
        return engine().eval(s);
    }

    /**
     * @see RythmEngine#shutdown()
     */
    public static void shutdown() {
        if (null == engine) return;
        engine.shutdown();
        engine = null;
    }

}
