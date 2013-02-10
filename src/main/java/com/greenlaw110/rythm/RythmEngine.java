package com.greenlaw110.rythm;

import com.greenlaw110.rythm.cache.ICacheService;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.exception.TagLoadException;
import com.greenlaw110.rythm.extension.IDurationParser;
import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.extension.Transformer;
import com.greenlaw110.rythm.internal.*;
import com.greenlaw110.rythm.internal.compiler.*;
import com.greenlaw110.rythm.internal.dialect.AutoToString;
import com.greenlaw110.rythm.internal.dialect.BasicRythm;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.internal.dialect.ToString;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.ILoggerFactory;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.logger.NullLogger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.StringTemplateResource;
import com.greenlaw110.rythm.resource.TemplateResourceManager;
import com.greenlaw110.rythm.resource.ToStringTemplateResource;
import com.greenlaw110.rythm.sandbox.SandboxExecutingService;
import com.greenlaw110.rythm.template.*;
import com.greenlaw110.rythm.toString.ToStringOption;
import com.greenlaw110.rythm.toString.ToStringStyle;
import com.greenlaw110.rythm.utils.F;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A Rythm Template Engine is the entry to the Rythm templating system. It provides a set of
 * APIs to render template. Each JVM allows multiple <code>RythmEngine</code> instance, with each
 * one represent a set of configurations.
 * <p/>
 * <p>The {@link Rythm} facade contains a default <code>RythmEngine</code> instance to make it
 * easy to use for most cases</p>
 */
public class RythmEngine implements IEventDispatcher {
    private static final ILogger logger = Logger.get(RythmEngine.class);

    /**
     * Rythm Engine Version. Used along with
     * {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#ENGINE_PLUGIN_VERSION plugin version} to
     * check if the cached template bytecode need to be refreshed or not
     * <p/>
     * TODO: use version marker and be substitute when build
     */
    private static final String version = "1.0-b2";

    private static final InheritableThreadLocal<RythmEngine> _engine = new InheritableThreadLocal<RythmEngine>();

    /**
     * Set the engine instance to a {@link ThreadLocal} variable, thus it is easy to
     * {@link #get() get} the current
     * <code>RythmEngine</code> dominating the rendering process.
     * <p/>
     * <p><b>Note</b>, this method is NOT an API to be called by user application</p>
     *
     * @param engine
     */
    public static void set(RythmEngine engine) {
        _engine.set(engine);
    }

    /**
     * Get the current engine instance from a {@link ThreadLocal} variable which is
     * {@link #set(RythmEngine) set} previously.
     * <p/>
     * <p><b>Note</b>, this method is NOT an API to be called by user application</p>
     *
     * @return
     */
    public static RythmEngine get() {
        return _engine.get();
    }


    /**
     * Check if the current rendering is dominated by a {@link Sandbox}
     * <p/>
     * <p><b>Note</b>, this method is NOT an API to be called by user application</p>
     *
     * @return true if the current thread is running in Sandbox mode
     */
    public static boolean insideSandbox() {
        return Sandbox.sandboxMode();
    }
    
    /* -----------------------------------------------------------------------------
      Fields and Accessors
    -------------------------------------------------------------------------------*/

    private RythmConfiguration _conf = null;

    /**
     * Return {@link RythmConfiguration configuration} of the engine
     * <p/>
     * <p>Usually user application should not call this method</p>
     *
     * @return
     */
    public RythmConfiguration conf() {
        if (null == _conf) {
            throw new IllegalStateException("Rythm engine not initialized");
        }
        return _conf;
    }

    /**
     * Return Version string of the engine instance. The version string
     * is composed by {@link #version Rythm version} and the
     * configured {@link RythmConfigurationKey#ENGINE_PLUGIN_VERSION plugin version}. The version
     * string will be used by Rythm to see if compiled bytecodes cached on disk should
     * be refreshed in an new version or not.
     * <p/>
     * <p><code>Note</code>, this method is not generally used by user application</p>
     *
     * @return
     */
    public String version() {
        return version + "-" + conf().pluginVersion();
    }

    private Rythm.Mode _mode = null;

    /**
     * Return the engine {@link Rythm.Mode mode}
     *
     * @return
     */
    public Rythm.Mode mode() {
        if (null == _mode) {
            _mode = conf().get(RythmConfigurationKey.ENGINE_MODE);
        }
        return _mode;
    }

    /**
     * Is this engine the default {@link Rythm#engine} instance?
     * <p/>
     * <p><b>Note</b>, not to be used by user application</p>
     *
     * @return
     */
    public boolean isSingleton() {
        return Rythm.engine == this;
    }

    /**
     * Is the engine running in {@link Rythm.Mode#prod product} mode?
     *
     * @return
     */
    public boolean isProdMode() {
        return mode() == Rythm.Mode.prod;
    }

    /**
     * Is the engine running in {@link Rythm.Mode#dev development} mode?
     *
     * @return
     */
    public boolean isDevMode() {
        return mode() != Rythm.Mode.prod;
    }

    private TemplateResourceManager _resourceManager;

    /**
     * Get {@link TemplateResourceManager resource manager} of the engine
     * <p/>
     * <p><b>Note</b>, this method should not be used by user application</p>
     *
     * @return
     */
    public TemplateResourceManager resourceManager() {
        return _resourceManager;
    }

    private TemplateClassManager _classes;

    /**
     * Get {@link TemplateClassManager template class manager} of the engine
     * <p/>
     * <p><b>Note</b>, this method should not be used by user application</p>
     *
     * @return
     */
    public TemplateClassManager classes() {
        return _classes;
    }

    private TemplateClassLoader _classLoader = null;

    /**
     * Get {@link TemplateClassLoader class loader} of the engine
     * <p/>
     * <p><b>Note</b>, this method should not be used by user application</p>
     *
     * @return
     */
    public TemplateClassLoader classLoader() {
        return _classLoader;
    }

    private TemplateClassCache _classCache = null;

    /**
     * Get {@link TemplateClassCache class cache} of the engine
     * <p/>
     * <p><b>Note</b>, this method should not be used by user application</p>
     *
     * @return
     */
    public TemplateClassCache classCache() {
        return _classCache;
    }

    private ExtensionManager _extensionManager;

    /**
     * Return {@link ExtensionManager} of this engine
     *
     * @return
     */
    public ExtensionManager extensionManager() {
        return _extensionManager;
    }

    private final DialectManager _dialectManager = new DialectManager();

    public DialectManager dialectManager() {
        return _dialectManager;
    }

    private ICacheService _cacheService = null;

    /* -----------------------------------------------------------------------------
      Constructors, Configuration and Initializing
    -------------------------------------------------------------------------------*/

    private void _initLogger(Map<String, ?> conf) {
        boolean logEnabled = (Boolean)RythmConfigurationKey.LOG_ENABLED.getConfiguration(conf);
        if (logEnabled) {
            ILoggerFactory factory = RythmConfigurationKey.LOG_FACTORY_IMPL.getConfiguration(conf);
            Logger.registerLoggerFactory(factory);
        } else {
            Logger.registerLoggerFactory(new NullLogger.Factory());
        }
    }

    private void _initConf(Map<String, ?> conf) {
        // load conf from disk
        Map<String, ?> rawConf = _loadConfFromDisk();

        // load conf from System.properties
        Properties sysProps = System.getProperties();
        rawConf.putAll((Map) sysProps);

        // load conf from user supplied configuration
        if (null != conf) rawConf.putAll((Map) conf);
        
        _initLogger(rawConf);

        // initialize the configuration with all loaded data 
        this._conf = new RythmConfiguration(rawConf);
    }

    /**
     * Create a rythm engine instance with default configuration
     *
     * @see com.greenlaw110.rythm.conf.RythmConfigurationKey
     */
    public RythmEngine() {
        init();
    }

    /**
     * Create a rythm engine instance with template root specified
     *
     * @param templateHome
     * @see com.greenlaw110.rythm.conf.RythmConfigurationKey
     */
    public RythmEngine(File templateHome) {
        _initConf(null);
        _conf.setTemplateHome(templateHome);
        init();
    }

    public RythmEngine(Properties userConfiguration) {
        this((Map) userConfiguration);
    }

    /**
     * Create a rythm engine instance with user supplied configuration data
     *
     * @param userConfiguration
     * @see com.greenlaw110.rythm.conf.RythmConfigurationKey
     */
    public RythmEngine(Map<String, ?> userConfiguration) {
        init(userConfiguration);
    }

    private void init() {
        init(null);
    }

    private Map _loadConfFromDisk() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) cl = Rythm.class.getClassLoader();
        URL url = cl.getResource("rythm.conf");
        if (null != url) {
            Properties p = new Properties();
            InputStream is = null;
            try {
                is = url.openStream();
                p.load(is);
                return p;
            } catch (Exception e) {
                logger.warn(e, "Error loading rythm.conf");
            } finally {
                try {
                    if (null != is) is.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        return new HashMap();
    }

    private void init(Map<String, ?> conf) {
        _initConf(conf);

        // post configuration initializations 
        _mode = _conf.get(RythmConfigurationKey.ENGINE_MODE);
        _classes = new TemplateClassManager(this);
        _classLoader = new TemplateClassLoader(this);
        _classCache = new TemplateClassCache(this);
        _resourceManager = new TemplateResourceManager(this);
        _extensionManager = new ExtensionManager(this);
        int ttl = (Integer) _conf.get(RythmConfigurationKey.DEFAULT_CACHE_TTL);
        _cacheService = _conf.get(RythmConfigurationKey.CACHE_SERVICE_IMPL);
        _cacheService.setDefaultTTL(ttl);


        // register built-in transformers if enabled
        boolean enableBuiltInJavaExtensions = (Boolean) _conf.get(RythmConfigurationKey.BUILT_IN_TRANSFORMER_ENABLED);
        if (enableBuiltInJavaExtensions) {
            registerTransformer("rythm", S.class);
        }

        boolean enableBuiltInTemplateLang = (Boolean) _conf.get(RythmConfigurationKey.BUILT_IN_TEMPLATE_LANG_ENABLED);
        if (enableBuiltInTemplateLang) {
            ExtensionManager em = extensionManager();
            em.registerTemplateLang(ILang.DefImpl.HTML);
            em.registerTemplateLang(ILang.DefImpl.JS);
            em.registerTemplateLang(ILang.DefImpl.JSON);
            em.registerTemplateLang(ILang.DefImpl.CSV);
            em.registerTemplateLang(ILang.DefImpl.CSS);
        }

        _tags.clear();
        _tags.put("chain", new JavaTagBase() {
            @Override
            protected void call(ParameterList params, Body body) {
                body.render(getBuffer());
            }
        });

        logger.debug("Rythm started in %s mode", mode());
    }

    /* -----------------------------------------------------------------------------
      Registrations
    -------------------------------------------------------------------------------*/

    /**
     * Register {@link Transformer transformers} using namespace specified in
     * the namespace {@link com.greenlaw110.rythm.extension.Transformer#value() value} defined in
     * the annotation.
     *
     * @param transformerClasses
     */
    public void registerTransformer(Class<?>... transformerClasses) {
        registerTransformer(null, transformerClasses);
    }

    /**
     * Register {@link Transformer transformers} using namespace specified to
     * replace the namespace {@link com.greenlaw110.rythm.extension.Transformer#value() value} defined in
     * the annotation.
     *
     * @param transformerClasses
     */
    public void registerTransformer(String namespace, Class<?>... transformerClasses) {
        ExtensionManager jem = extensionManager();
        for (Class<?> extensionClass : transformerClasses) {
            Transformer t = extensionClass.getAnnotation(Transformer.class);
            boolean classAnnotated = null != t;
            String nmsp = namespace;
            boolean namespaceIsEmpty = S.empty(namespace);
            if (classAnnotated && namespaceIsEmpty) {
                nmsp = t.value();
            }
            for (Method m : extensionClass.getDeclaredMethods()) {
                int flag = m.getModifiers();
                if (!Modifier.isPublic(flag) || !Modifier.isStatic(flag)) continue;
                int len = m.getParameterTypes().length;
                if (len <= 0) continue;

                Transformer tm = m.getAnnotation(Transformer.class);
                boolean methodAnnotated = null != tm;
                if (!methodAnnotated && !classAnnotated) continue;
                
                String mnmsp = nmsp;
                if (methodAnnotated && namespaceIsEmpty) {
                    mnmsp = tm.value();
                }

                String cn = extensionClass.getSimpleName();
                String cn0 = extensionClass.getName();
                String mn = m.getName();
                String fullName = String.format("%s.%s", cn0, mn);
                if (S.notEmpty(mnmsp) && !"rythm".equals(mnmsp)) {
                    mn = mnmsp + "_" + mn;
                }
                if (len == 1) {
                    jem.registerJavaExtension(new IJavaExtension.VoidParameterExtension(cn, mn, fullName));
                } else {
                    jem.registerJavaExtension(new IJavaExtension.ParameterExtension(cn, mn, ".+", fullName));
                }
            }
        }
    }

    /* -----------------------------------------------------------------------------
      Rendering methods and APIs
    -------------------------------------------------------------------------------*/

    private void setRenderArgs(ITemplate t, Object... args) {
        if (1 == args.length) {
            Object o0 = args[0];
            if (o0 instanceof Map) {
                t.setRenderArgs((Map<String, Object>) args[0]);
            } else if (o0 instanceof JSONWrapper) {
                t.setRenderArg((JSONWrapper) o0);
            } else {
                t.setRenderArgs(args);
            }
        } else {
            t.setRenderArgs(args);
        }
        //if (mode.isDev()) cceCounter.remove();
    }

    @Deprecated
    private void handleCCE(ClassCastException ce) {
//        Integer I = cceCounter.get();
//        if (null == I) {
//            I = 0;
//            cceCounter.set(1);
//        } else {
//            I++;
//            cceCounter.set(I);
//        }
//        if (I > 2) {
//            cceCounter.remove();
//            throw ce;
//        }
//        restart(ce);
    }

    //static ThreadLocal<Integer> cceCounter = new ThreadLocal<Integer>();

    private ITemplate getTemplate(IDialect dialect, String template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);

        TemplateClass tc = classes().getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(template, this, dialect);
        }
        ITemplate t = tc.asTemplate();
        setRenderArgs(t, args);
//        try {
//            setRenderArgs(t, args);
//        } catch (ClassCastException ce) {
//            if (mode.isDev()) {
//                handleCCE(ce);
//                return getTemplate(dialect, template, args);
//            }
//            throw ce;
//        }
        return t;
    }

    /**
     * Get an new {@link ITemplate template} instance from a String and an array
     * of render args. The string parameter could be either a template file path
     * or the inline template source content.
     * <p/>
     * <p>When the args array contains only one element and is of {@link java.util.Map} type
     * the the render args are passed to template
     * {@link ITemplate#setRenderArgs(java.util.Map) by name},
     * otherwise they passes to template instance by position</p>
     *
     * @param template
     * @param args
     * @return
     */
    @SuppressWarnings("unchecked")
    public ITemplate getTemplate(String template, Object... args) {
        return getTemplate(null, template, args);
    }

    /**
     * Get an new template instance by template source {@link java.io.File file}
     * and an array of arguments.
     *
     * @param file the template source file
     * @param args the render args. See {@link #getTemplate(String, Object...)}
     * @return
     */
    @SuppressWarnings("unchecked")
    public ITemplate getTemplate(File file, Object... args) {
        ParamTypeInferencer.registerParams(this, args);

        TemplateClass tc = classes().getByTemplate(resourceManager().get(file).getKey());
        if (null == tc) {
            tc = new TemplateClass(file, this);
        }
        ITemplate t = tc.asTemplate();
        if (null == t) return null;
        setRenderArgs(t, args);
//        try {
//            setRenderArgs(t, args);
//        } catch (ClassCastException ce) {
//            if (mode.isDev()) {
//                handleCCE(ce);
//                return getTemplate(template, args);
//            }
//            throw ce;
//        }
//        if (mode.isDev()) cceCounter.remove();
        return t;
    }

    /**
     * Render template by string parameter and an array of
     * template args. The string parameter could be either
     * a path point to the template source file, or the inline
     * template source content. The render result is returned
     * as a String
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param template either the path of template source file or inline template content
     * @param args     render args array
     * @return
     */
    public String render(String template, Object... args) {
        ITemplate t = getTemplate(template, args);
        return t.render();
    }

    /**
     * Render template by string parameter and an array of
     * template args. The string parameter could be either
     * a path point to the template source file, or the inline
     * template source content. The render result is output
     * to the specified binary output stream
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param os       the output stream
     * @param template either the path of template source file or inline template content
     * @param args     render args array
     */
    public void render(OutputStream os, String template, Object... args) {
        outputMode.set(OutputMode.os);
        ITemplate t = getTemplate(template, args);
        t.render(os);
    }

    /**
     * Render template by string parameter and an array of
     * template args. The string parameter could be either
     * a path point to the template source file, or the inline
     * template source content. The render result is output
     * to the specified character based writer
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param w        the writer
     * @param template either the path of template source file or inline template content
     * @param args     render args array
     */
    public void render(Writer w, String template, Object... args) {
        outputMode.set(OutputMode.writer);
        ITemplate t = getTemplate(template, args);
        t.render(w);
    }

    /**
     * Render template with source specified by {@link java.io.File file instance}
     * and an array of render args. Render result return as a String
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param file the template source file
     * @param args render args array
     * @return
     */
    public String render(File file, Object... args) {
        ITemplate t = getTemplate(file, args);
        return t.render();
    }

    /**
     * Render template with source specified by {@link java.io.File file instance}
     * and an array of render args. Render result output into the specified binary
     * {@link java.io.OutputStream}
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param os   the output stream
     * @param file the template source file
     * @param args render args array
     * @return
     */
    public void render(OutputStream os, File file, Object... args) {
        outputMode.set(OutputMode.os);
        ITemplate t = getTemplate(file, args);
        t.render(os);
    }

    /**
     * Render template with source specified by {@link java.io.File file instance}
     * and an array of render args. Render result output into the specified binary
     * {@link java.io.Writer}
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param w    the writer
     * @param file the template source file
     * @param args render args array
     * @return
     */
    public void render(Writer w, File file, Object... args) {
        outputMode.set(OutputMode.writer);
        ITemplate t = getTemplate(file, args);
        t.render(w);
    }

    /**
     * Render template by string typed inline template content and an array of
     * template args. The render result is returned as a String
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param template the inline template content
     * @param args     the render args array
     */
    public String renderStr(String template, Object... args) {
        return renderString(template, args);
    }

    /**
     * Alias of {@link #renderString(String, Object...)}
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param template the inline template content
     * @param args     the render args array
     * @return
     */
    @SuppressWarnings("unchecked")
    public String renderString(String template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);

        TemplateClass tc = classes().getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(new StringTemplateResource(template), this);
        }
        ITemplate t = tc.asTemplate();
        setRenderArgs(t, args);
        return t.render();
    }

    /**
     * Render template in substitute mode by string typed template source
     * and an array of render args. The string parameter could be either
     * a path point to the template source file, or the inline
     * template source content. Return render result as String
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param template either the template source file path or the inline template content
     * @param args     the render args array
     * @return
     */
    public String substitute(String template, Object... args) {
        ITemplate t = getTemplate(BasicRythm.INSTANCE, template, args);
        return t.render();
    }

    /**
     * Render template in substitute mode by File typed template source
     * and an array of render args. Return render result as String
     * <p/>
     * <p>See {@link #getTemplate(java.io.File, Object...)} for note on
     * render args</p>
     *
     * @param file the template source file
     * @param args the render args array
     * @return
     */
    public String substitute(File file, Object... args) {
        ITemplate t = getTemplate(file, args, BasicRythm.INSTANCE);
        return t.render();
    }

    /**
     * Render template in ToString mode by string typed template source
     * and one object instance which state is to be output.
     * The string parameter could be either a path point to the template
     * source file, or the inline template source content. Return render
     * result as String
     *
     * @param template either the template source file path or the inline template content
     * @param obj      the object instance which state is to be output as a string
     * @return
     */
    public String toString(String template, Object obj) {
        Class argClass = obj.getClass();
        String clsName = argClass.getName();
        if (clsName.matches(".*\\$[0-9].*")) {
            argClass = obj.getClass().getSuperclass();
        }
        String key = template + argClass;
        TemplateClass tc = classes().getByTemplate(key);
        if (null == tc) {
            tc = new TemplateClass(template, this, new ToString(argClass));
            classes().tmplIdx.put(key, tc);
        }
        ITemplate t = tc.asTemplate();
        t.setRenderArg(0, obj);
        return t.render();
    }

    /**
     * Render template in AutoToString mode by one object instance which state is
     * to be output. Return render result as String
     *
     * @param obj the object instance which state is to be output as a string
     * @return
     */
    public String toString(Object obj) {
        return toString(obj, ToStringOption.DEFAULT_OPTION, (ToStringStyle) null);
    }

    /**
     * Render template in AutoToString mode by one object instance which state is
     * to be output and {@link ToStringOption option} and {@link ToStringStyle stype}.
     * Return render result as String
     *
     * @param obj    the object instance which state is to be output as a string
     * @param option the output option
     * @param style  the output style
     * @return
     */
    public String toString(Object obj, ToStringOption option, ToStringStyle style) {
        Class<?> c = obj.getClass();
        AutoToString.AutoToStringData key = new AutoToString.AutoToStringData(c, option, style);
        //String template = AutoToString.templateStr(c, option, style);
        TemplateClass tc = classes().getByTemplate(key);
        if (null == tc) {
            tc = new TemplateClass(new ToStringTemplateResource(key), this, new AutoToString(c, key));
            classes().tmplIdx.put(key, tc);
        }
        ITemplate t = tc.asTemplate();
        t.setRenderArg(0, obj);
        return t.render();
    }

    /**
     * Render template in AutoToString mode by one object instance which state is
     * to be output and {@link ToStringOption option} and
     * {@link org.apache.commons.lang3.builder.ToStringStyle apache commons to string stype}.
     * Return render result as String
     *
     * @param obj    the object instance which state is to be output as a string
     * @param option the output option
     * @param style  the output style specified as apache commons ToStringStyle
     * @return
     */
    public String commonsToString(Object obj, ToStringOption option, org.apache.commons.lang3.builder.ToStringStyle style) {
        return toString(obj, option, ToStringStyle.fromApacheStyle(style));
    }

    private Set<String> nonExistsTemplates = new HashSet<String>();

    private class NonExistsTemplatesChecker {
        boolean started = false;
        private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

        NonExistsTemplatesChecker() {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    List<String> toBeRemoved = new ArrayList<String>();
                    for (String template : nonExistsTemplates) {
                        ITemplateResource rsrc = resourceManager().getFileResource(template);
                        if (rsrc.isValid()) {
                            toBeRemoved.add(template);
                        }
                    }
                    nonExistsTemplates.removeAll(toBeRemoved);
                    toBeRemoved.clear();
                    TemplateClass tc = classes().all().get(0);
                    for (String tag : _nonExistsTags) {
                        if (null != resourceManager().tryLoadTag(tag, tc)) {
                            toBeRemoved.add(tag);
                        }
                    }
                    _nonExistsTags.removeAll(toBeRemoved);
                    toBeRemoved.clear();
                }
            }, 0, 1000 * 10, TimeUnit.MILLISECONDS);
        }
    }

    private NonExistsTemplatesChecker nonExistsTemplatesChecker = null;

    /**
     * Render template if specified template exists, otherwise return empty string
     *
     * @param template the template source path
     * @param args     render args. See {@link #getTemplate(String, Object...)}
     * @return
     */
    public String renderIfTemplateExists(String template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);

        if (nonExistsTemplates.contains(template)) return "";
        TemplateClass tc = classes().getByTemplate(template);
        if (null == tc) {
            ITemplateResource rsrc = resourceManager().getFileResource(template);
            if (rsrc.isValid()) {
                tc = new TemplateClass(rsrc, this);
            } else {
                nonExistsTemplates.add(template);
                if (mode().isDev() && nonExistsTemplatesChecker == null) {
                    nonExistsTemplatesChecker = new NonExistsTemplatesChecker();
                }
                return "";
            }
        }
        ITemplate t = tc.asTemplate();
        setRenderArgs(t, args);
        return t.render();
    }

    /* -----------------------------------------------------------------------------
      Tags
    -------------------------------------------------------------------------------*/

    private final Map<String, ITag> _tags = new HashMap<String, ITag>();
    private final Set<String> _nonTags = new HashSet<String>();

    /**
     * Whether a {@link ITag tag} is registered to the engine by name specified
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param tagName
     * @return
     */
    public boolean hasTag(String tagName) {
        return _tags.containsKey(tagName);
    }

    /**
     * Get a {@link ITag tag} registered to the engine by name
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param tagName
     * @return
     */
    public ITag getTag(String tagName) {
        return _tags.get(tagName);
    }

    /**
     * Return {@link TemplateClass} from a tag name
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param name
     * @return
     */
    public TemplateClass getTemplateClassFromTagName(String name) {
        TemplateBase tag = (TemplateBase) _tags.get(name);
        if (null == tag) return null;
        return tag.getTemplateClass(false);
    }

    /**
     * Check if a tag exists and return it's tag name
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param name
     * @param tc
     * @return
     */
    public String testTag(String name, TemplateClass tc) {
        if (Keyword.THIS.toString().equals(name)) {
            return resourceManager().getFullTagName(tc);
        }
        if (mode().isProd() && _nonTags.contains(name)) return null;
        boolean isTag = _tags.containsKey(name);
        if (isTag) return name;
        // try imported path
        if (null != tc.importPaths) {
            for (String s : tc.importPaths) {
                String name0 = s + "." + name;
                if (_tags.containsKey(name0)) return name0;
            }
        }
        // try relative path
        String callerName = resourceManager().getFullTagName(tc);
        int pos = callerName.lastIndexOf(".");
        if (-1 != pos) {
            String name0 = callerName.substring(0, pos) + "." + name;
            if (_tags.containsKey(name0)) return name0;
        }

        try {
            // try to ask resource manager
            TemplateClass tagTC = resourceManager().tryLoadTag(name, tc);
            if (null == tagTC) {
                if (mode().isProd()) _nonTags.add(name);
                return null;
            }
            String fullName = tagTC.getFullName();
            return fullName;
        } catch (TagLoadException e) {
            throw e;
        } catch (RythmException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e, "error trying load tag[%s]", name);
            // see if the
        }
        return null;
    }

    /**
     * Register a tag class. If there is name collision then registration
     * will fail
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @return true if registration success
     */
    public boolean registerTag(ITag tag) {
        String name = tag.getName();
        return registerTag(name, tag);
    }

    /**
     * Register a tag using the given name
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param name
     * @param tag
     * @return false if registration failed
     */
    public boolean registerTag(String name, ITag tag) {
        if (null == tag) throw new NullPointerException();
        if (_tags.containsKey(name)) {
            return false;
        }
        _tags.put(name, tag);
        logger.trace("tag %s registered", name);
        return true;
    }

    /**
     * Invoke a tag
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param line
     * @param name
     * @param caller
     * @param params
     * @param body
     * @param context
     */
    public void invokeTag(int line, String name, ITemplate caller, ITag.ParameterList params, ITag.Body body, ITag.Body context) {
        invokeTag(line, name, caller, params, body, context, false);
    }

    private Set<String> _nonExistsTags = new HashSet<String>();

    /**
     * Invoke a tag
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param line
     * @param name
     * @param caller
     * @param params
     * @param body
     * @param context
     * @param ignoreNonExistsTag
     */
    public void invokeTag(int line, String name, ITemplate caller, ITag.ParameterList params, ITag.Body body, ITag.Body context, boolean ignoreNonExistsTag) {
        if (_nonExistsTags.contains(name)) return;
        // try tag registry first
        ITag tag = _tags.get(name);
        TemplateClass tc = ((TemplateBase) caller).getTemplateClass(true);
        if (null == tag) {
            // is calling self
            if (S.isEqual(name, ((TagBase) caller).getName())) tag = (TagBase) caller;
        }

        if (null == tag) {
            // try imported path
            if (null != tc.importPaths) {
                for (String s : tc.importPaths) {
                    String name0 = s + "." + name;
                    tag = _tags.get(name0);
                    if (null != tag) break;
                }
            }

            // try relative path
            if (null == tag) {
                String callerName = resourceManager().getFullTagName(tc);
                int pos = callerName.lastIndexOf(".");
                if (-1 != pos) {
                    String name0 = callerName.substring(0, pos) + "." + name;
                    tag = _tags.get(name0);
                }
            }

            // try load the tag from resource
            if (null == tag) {
                TemplateClass tagC = resourceManager().tryLoadTag(name, tc);
                if (null != tagC) tag = _tags.get(tagC.getFullName());
                if (null == tag) {
                    if (ignoreNonExistsTag) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("cannot find tag: " + name);
                        }
                        _nonExistsTags.add(name);
                        if (mode().isDev() && nonExistsTemplatesChecker == null) {
                            nonExistsTemplatesChecker = new NonExistsTemplatesChecker();
                        }
                        return;
                    } else {
                        throw new NullPointerException("cannot find tag: " + name);
                    }
                }
                tag = (ITag) tag.cloneMe(this, caller);
            }
        }

        if (!(tag instanceof JavaTagBase)) {
            // try refresh the tag loaded from template file under tag root
            // note Java source tags are not reloaded here
            String cn = tag.getClass().getName();
            /*
            if (reloadByIncClassVersion() && -1 == cn.indexOf("$")) {
                int pos = cn.lastIndexOf("v");
                if (-1 < pos) cn = cn.substring(0, pos);
            }
            */
            TemplateClass tc0 = classes().getByClassName(cn);
            if (null == tc0) {
                System.out.println(tag.getClass());
                System.out.println(name);
                System.out.println(cn);
                System.out.println(caller.getClass());
            }
            tag = (ITag) tc0.asTemplate(caller);
        } else {
            tag = (ITag) tag.cloneMe(this, caller);
        }

        if (null != params) {
            if (tag instanceof JavaTagBase) {
                ((JavaTagBase) tag).setRenderArgs(params);
            } else {
                for (int i = 0; i < params.size(); ++i) {
                    ITag.Parameter param = params.get(i);
                    if (null != param.name) tag.setRenderArg(param.name, param.value);
                    else tag.setRenderArg(i, param.value);
                }
            }
        }
        if (null != body) tag.setRenderArg("_body", body);
        RythmEvents.ON_TAG_INVOCATION.trigger(this, F.T2((TemplateBase) caller, tag));
        try {
            if (null != context) {
                ((TagBase) tag).setBodyContext(context);
            }
            tag.call(line);
        } finally {
            RythmEvents.TAG_INVOKED.trigger(this, F.T2((TemplateBase) caller, tag));
        }
    }

    // -- cache api

    /**
     * Cache object using key and args for ttl seconds
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param key
     * @param o
     * @param ttl  if zero then defaultTTL used, if negative then never expire
     * @param args
     */
    public void cache(String key, Object o, int ttl, Object... args) {
        if (conf().cacheDisabled()) return;
        ICacheService cacheService = _cacheService;
        Serializable value = null == o ? "" : (o instanceof Serializable ? (Serializable) o : o.toString());
        if (args.length > 0) {
            StringBuilder sb = new StringBuilder(key);
            for (Object arg : args) {
                sb.append("-").append(arg);
            }
            key = sb.toString();
        }
        cacheService.put(key, value, ttl);
    }

    /**
     * Store object o into cache service with ttl equals to duration specified.
     * <p/>
     * <p>The duration is a string to be parsed by @{link #durationParser}</p>
     * <p/>
     * <p>The object o is associated with given key and a list of argument values</p>
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param key
     * @param o
     * @param duration
     * @param args
     */
    public void cache(String key, Object o, String duration, Object... args) {
        if (conf().cacheDisabled()) return;
        IDurationParser dp = conf().durationParser();
        int ttl = null == duration ? 0 : dp.parseDuration(duration);
        cache(key, o, ttl, args);
    }

    /**
     * Get cached value using key and a list of argument values
     * <p/>
     * <p>Not an API for user application</p>
     *
     * @param key
     * @param args
     * @return
     */
    public Serializable cached(String key, Object... args) {
        if (conf().cacheDisabled()) return null;
        ICacheService cacheService = _cacheService;
        if (args.length > 0) {
            StringBuilder sb = new StringBuilder(key);
            for (Object arg : args) {
                sb.append("-").append(arg);
            }
            key = sb.toString();
        }
        return cacheService.get(key);
    }

    // -- SPI interface
    // -- issue #47
    private Map<TemplateClass, Set<TemplateClass>> extendMap = new HashMap<TemplateClass, Set<TemplateClass>>();

    /**
     * Not an API for user application
     *
     * @param parent
     * @param child
     */
    public void addExtendRelationship(TemplateClass parent, TemplateClass child) {
        if (mode().isProd()) return;
        Set<TemplateClass> children = extendMap.get(parent);
        if (null == children) {
            children = new HashSet<TemplateClass>();
            extendMap.put(parent, children);
        }
        children.add(child);
    }

    /**
     * Not an API for user application
     *
     * @param parent
     */
    // called to invalidate all template class which extends the parent
    public void invalidate(TemplateClass parent) {
        if (mode().isProd()) return;
        Set<TemplateClass> children = extendMap.get(parent);
        if (null == children) return;
        for (TemplateClass child : children) {
            invalidate(child);
            child.reset();
        }
    }

    // -- Sandbox

    private SandboxExecutingService _secureExecutor = null;

    private SandboxExecutingService secureExecutor() {
        if (null == _secureExecutor) {
            int poolSize = (Integer) conf().get(RythmConfigurationKey.SANDBOX_POOL_SIZE);
            SecurityManager sm = conf().get(RythmConfigurationKey.SANDBOX_SECURITY_MANAGER_IMPL);
            int timeout = (Integer) conf().get(RythmConfigurationKey.SANDBOX_TIMEOUT);
            _secureExecutor = new SandboxExecutingService(poolSize, sm, timeout);
        }
        return _secureExecutor;
    }

    /**
     * Create a {@link Sandbox} instance to render the template
     *
     * @return
     */
    public Sandbox sandbox() {
        return new Sandbox(this, secureExecutor());
    }

    // dispatch rythm events
    private IEventDispatcher eventDispatcher = null;

    private IEventDispatcher eventDispatcher() {
        if (null == eventDispatcher) {
            eventDispatcher = new EventBus(this);
        }
        return eventDispatcher;
    }

    /**
     * Not an API for user application
     *
     * @param event
     * @param param
     * @return
     */
    @Override
    public Object accept(IEvent event, Object param) {
        return eventDispatcher().accept(event, param);
    }

    /* -----------------------------------------------------------------------------
      Output Mode
    -------------------------------------------------------------------------------*/

    /**
     * Defines the output method for render result.
     * <ul>
     * <li>os: output to binary {@link java.io.OutputStream}</li>
     * <li>writer: output to character based {@link java.io.Writer}</li>
     * <li>str: return render result as a {@link java.lang.String}</li>
     * </ul>
     * <p/>
     * <p>This is not an API used in user application</p>
     */
    public static enum OutputMode {
        os, writer, str {
            @Override
            public boolean writeOutput() {
                return false;
            }
        };

        /**
         * Return true if the current output mode is to output to {@link java.io.OutputStream}
         * or {@link java.io.Writer}
         *
         * @return
         */
        public boolean writeOutput() {
            return true;
        }
    }

    private final static InheritableThreadLocal<OutputMode> outputMode = new InheritableThreadLocal<OutputMode>() {
        @Override
        protected OutputMode initialValue() {
            return OutputMode.str;
        }
    };

    /**
     * Return current {@link OutputMode}. Not to be used in user application
     *
     * @return
     */
    public final static OutputMode outputMode() {
        return outputMode.get();
    }

    /* -----------------------------------------------------------------------------
      Restart and Shutdown
    -------------------------------------------------------------------------------*/

    /**
     * Restart the engine with an exception as the cause.
     * <p><b>Note</b>, this is not supposed to be called by user application</p>
     *
     * @param cause
     */
    public void restart(RuntimeException cause) {
        if (isProdMode()) throw cause;
        if (!(cause instanceof ClassReloadException)) {
            String msg = cause.getMessage();
            if (cause instanceof RythmException) {
                RythmException re = (RythmException) cause;
                msg = re.getSimpleMessage();
            }
            logger.warn("restarting rythm engine due to %s", msg);
        }
        restart();
    }

    private void restart() {
        if (isProdMode()) return;
        _classLoader = new TemplateClassLoader(this);

        // clear all template tags which is managed by TemplateClassManager
        List<String> templateTags = new ArrayList<String>();
        for (String name : _tags.keySet()) {
            ITag tag = _tags.get(name);
            if (!(tag instanceof JavaTagBase)) {
                templateTags.add(name);
            }
        }
        for (String name : templateTags) {
            _tags.remove(name);
        }
    }

    interface IShutdownListener {
        void onShutdown();
    }

    private IShutdownListener shutdownListener = null;

    void setShutdownListener(IShutdownListener listener) {
        this.shutdownListener = listener;
    }

    /**
     * Shutdown this rythm engine
     */
    public void shutdown() {
        if (null != _cacheService) _cacheService.shutdown();
        if (null != _secureExecutor) _secureExecutor.shutdown();
        if (null != shutdownListener) shutdownListener.onShutdown();
    }

}
