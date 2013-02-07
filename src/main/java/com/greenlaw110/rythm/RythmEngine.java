package com.greenlaw110.rythm;

import com.greenlaw110.rythm.cache.ICacheService;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.exception.TagLoadException;
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
import com.greenlaw110.rythm.resource.*;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.sandbox.SandboxExecutingService;
import com.greenlaw110.rythm.extension.*;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.JavaTagBase;
import com.greenlaw110.rythm.template.TagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.toString.ToStringOption;
import com.greenlaw110.rythm.toString.ToStringStyle;
import com.greenlaw110.rythm.utils.*;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A Rythm Template Engine is the entry to the Rythm templating system. It provides a set of
 * APIs to render template. Each JVM allows multiple <code>RythmEngine</code> instance, with each
 * one represent a set of configurations.
 * 
 * <p>The {@link Rythm} facade contains a default <code>RythmEngine</code> instance to make it
 * easy to use for most cases</p>
 */
public class RythmEngine implements IEventDispatcher {
    private static final ILogger logger = Logger.get(RythmEngine.class);

    /**
     * Rythm Engine Version. Used along with 
     * {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#ENGINE_PLUGIN_VERSION plugin version} to
     * check if the cached template bytecode need to be refreshed or not
     * 
     * TODO: use version marker and be substitute when build 
     */
    private static final String version = "1.0-b2";
    
    private static final InheritableThreadLocal<RythmEngine> _engine = new InheritableThreadLocal<RythmEngine>();

    /**
     * Set the engine instance to a {@link ThreadLocal} variable, thus it is easy to 
     * {@link #get() get} the current 
     * <code>RythmEngine</code> dominating the rendering process.
     * 
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
     * 
     * <p><b>Note</b>, this method is NOT an API to be called by user application</p>
     * 
     * @return
     */
    public static RythmEngine get() {
        return _engine.get();
    }
    

    /**
     * Check if the current rendering is dominated by a {@link Sandbox}
     * 
     * <p><b>Note</b>, this method is NOT an API to be called by user application</p>
     * 
     * @return true if the current thread is running in Sandbox mode
     */
    public static boolean insideSandbox() {
        return Sandbox.sandboxMode();
    }

    private RythmConfiguration _conf = null;

    /**
     * Return {@link RythmConfiguration configuration} of the engine
     * 
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
     * 
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
     * 
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
     * @return
     */
    public boolean isDevMode() {
        return mode() != Rythm.Mode.prod;
    }

    private final TemplateResourceManager _resourceManager = new TemplateResourceManager(this);

    /**
     * Get {@link TemplateResourceManager resource manager} of the engine
     * 
     * <p><b>Note</b>, this method should not be used by user application</p>
     * 
     * @return
     */
    public TemplateResourceManager resourceManager() {
        return _resourceManager;
    }
    
    private final TemplateClassManager _classes = new TemplateClassManager(this);

    /**
     * Get {@link TemplateClassManager template class manager} of the engine
     * 
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
     * 
     * <p><b>Note</b>, this method should not be used by user application</p>
     * 
     * @return
     */
    public TemplateClassLoader classLoader() {
        return _classLoader;
    }
    
    private TemplateClassCache _classCache = new TemplateClassCache(this);

    /**
     * Get {@link TemplateClassCache class cache} of the engine
     * 
     * <p><b>Note</b>, this method should not be used by user application</p>
     * 
     * @return
     */
    public TemplateClassCache classCache() {
        return _classCache;
    }

    private void initConf() {
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

     * @see com.greenlaw110.rythm.conf.RythmConfigurationKey
     * @param templateHome
     */
    public RythmEngine(File templateHome) {
        initConf();
        _conf.setTemplateHome(templateHome);
        init();
    }

    /**
     * Create a rythm engine instance with user supplied configuration data
     * 
     * @see com.greenlaw110.rythm.conf.RythmConfigurationKey 
     * @param userConfiguration
     */
    public RythmEngine(Map<String, ?> userConfiguration) {
        init(userConfiguration);
    }

    private void init() {
        init(null);
    }
    
    private Map loadConfFromDisk() {
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

    public void init(Map<String, ?> conf) {
        // load conf from disk
        Map<String, ?> rawConf = loadConfFromDisk();
        
        // load conf from System.properties
        Properties sysProps = System.getProperties();
        rawConf.putAll((Map)sysProps);
        
        // load conf from user supplied configuration
        if (null != conf) rawConf.putAll((Map)conf);
        
        // initialize the configuration with all loaded data 
        this._conf = new RythmConfiguration(conf);

        _mode = _conf.get(RythmConfigurationKey.ENGINE_MODE);
        enableJavaExtensions = configuration.getAsBoolean("rythm.enableJavaExtensions", true);
        boolean disableBuiltInJavaExtensions = configuration.getAsBoolean("rythm.disableBuiltInJavaExtensions", false);
        if (!disableBuiltInJavaExtensions) {
            registerJavaExtension(S.class);
        }
        enableTypeInference = configuration.getAsBoolean("rythm.enableTypeInference", false);
        enableSmartEscape = configuration.getAsBoolean("rythm.enableSmartEscape", true);
        enableNaturalTemplate = configuration.getAsBoolean("rythm.enableNaturalTemplate", true);
        defaultLang = configuration.getAs("rythm.defLang", ILang.DefImpl.HTML, ILang.class);
        noFileWrite = configuration.getAsBoolean("rythm.noFileWrite", false);
        tmpDir = noFileWrite ? null : configuration.getAsFile("rythm.tmpDir", IO.tmpDir());
        // if templateHome set to null then it assumes use ClasspathTemplateResource by default
        templateHome = configuration.getAsFile("rythm.root", defaultRoot());
        compactMode = configuration.getAsBoolean("rythm.compactOutput", isProdMode());
        preCompiledHome = configuration.getAsFile("rythm.preCompiled.root", null);
        
        boolean disableBuiltInTemplateLang = configuration.getAsBoolean("rythm.disableBuildInTemplateLang", false);
        if (!disableBuiltInTemplateLang){
            ExtensionManager em = getExtensionManager();
            em.registerTemplateLang(ILang.DefImpl.HTML);
            em.registerTemplateLang(ILang.DefImpl.JS);
            em.registerTemplateLang(ILang.DefImpl.JSON);
            em.registerTemplateLang(ILang.DefImpl.CSV);
            em.registerTemplateLang(ILang.DefImpl.CSS);
        } 

        defaultTTL = configuration.getAsInt("rythm.cache.defaultTTL", 60 * 60);
        cacheService = configuration.getAsCacheService("rythm.cache.service");
        cacheService.setDefaultTTL(defaultTTL);
        durationParser = configuration.getAsDurationParser("rythm.cache.durationParser");

        cl = configuration.getAs("rythm.classLoader.parent", cl, ClassLoader.class);
        classLoader = new TemplateClassLoader(cl, this);
        classes.clear();
        tags.clear();
        tags.put("chain", new JavaTagBase() {
            @Override
            protected void call(ParameterList params, Body body) {
                body.render(getOut());
            }
        });
        //defaultRenderArgs = configuration.getAs("rythm.defaultRenderArgs", null, Map.class);

        Object o = configuration.get("rythm.resource.loader");
        if (o instanceof ITemplateResourceLoader) {
            resourceManager.resourceLoader = (ITemplateResourceLoader) o;
        } else if (o instanceof String) {
            try {
                resourceManager.resourceLoader = (ITemplateResourceLoader) Class.forName(((String) o)).newInstance();
            } catch (Exception e) {
                logger.warn("invalid resource loader class");
            }
        }
        
        String s = configuration.getProperty("rythm.restrictedClasses", "");
        s += ";com.greenlaw110.rythm.Rythm;com.greenlaw110.rythm.RythmEngine;java.io;java.nio;java.security;java.rmi;java.net;java.awt;java.applet";
        String[] sa = s.split("[\\s;:,]+");
        restrictedClasses.clear();
        for (int i = 0, j = sa.length; i < j; ++i) {
            s = sa[i].trim();
            if (!"".equals(s)) restrictedClasses.add(s);
        }
        

//        if (null != tagHome && configuration.getAsBoolean("rythm.tag.autoscan", true)) {
//            loadTags();
//        }

        logger.debug("Rythm started in %s mode", mode);
    }

    public void restart(RuntimeException cause) {
        if (isProdMode()) throw cause;
        if (!(cause instanceof ClassReloadException)) {
            String msg = cause.getMessage();
            if (cause instanceof RythmException) {
                RythmException re = (RythmException)cause;
                msg = re.getSimpleMessage();
            }
            logger.warn("restarting rythm engine due to %s", msg);
        }
        restart();
    }

    private void restart() {
        if (isProdMode()) return;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        cl = configuration.getAs("rythm.classLoader.parent", cl, ClassLoader.class);
        classLoader = new TemplateClassLoader(cl, this);

        // clear all template tags which is managed by TemplateClassManager
        List<String> templateTags = new ArrayList<String>();
        for (String name : tags.keySet()) {
            ITag tag = tags.get(name);
            if (!(tag instanceof JavaTagBase)) {
                templateTags.add(name);
            }
        }
        for (String name : templateTags) {
            tags.remove(name);
        }
    }


    private void setRenderArgs(ITemplate t, Object ... args) {
        if (1 == args.length) {
            Object o0 = args[0];
            if (o0 instanceof Map) {
                t.setRenderArgs((Map<String, Object>) args[0]);
            } else if (o0 instanceof JSONWrapper) {
                t.setRenderArg((JSONWrapper)o0);
            } else {
                t.setRenderArgs(args);
            }
        } else {
            t.setRenderArgs(args);
        }
        //if (mode.isDev()) cceCounter.remove();
    }
    
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

    static ThreadLocal<Integer> cceCounter = new ThreadLocal<Integer>();
    @SuppressWarnings("unchecked")
    public ITemplate getTemplate(File template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);
    
        TemplateClass tc = classes.getByTemplate(resourceManager.get(template).getKey());
        if (null == tc) {
            tc = new TemplateClass(template, this);
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
    
    public static enum OutputMode {
        os, writer, str {
            @Override
            public boolean writeOutput() {
                return false;
            }
        };
        
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
    
    public final static OutputMode outputMode() {
        return outputMode.get();
    }
    
    private ITemplate getTemplate(IDialect dialect, String template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);
        
        TemplateClass tc = classes.getByTemplate(template);
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

    @SuppressWarnings("unchecked")
    public ITemplate getTemplate(String template, Object... args) {
        return getTemplate(null, template, args);
    }

    public void preprocess(ITemplate t) {
        IImplicitRenderArgProvider p = conf().implicitRenderArgProvider();
        if (null != p) p.setRenderArgs(t);
    }

    public String render(String template, Object... args) {
        ITemplate t = getTemplate(template, args);
        return t.render();
    }
    
    public void render(OutputStream os, String template, Object... args) {
        outputMode.set(OutputMode.os);
        ITemplate t = getTemplate(template, args);
        t.render(os);
    }
    
    public void render(Writer w, String template, Object... args) {
        outputMode.set(OutputMode.writer);
        ITemplate t = getTemplate(template, args);
        t.render(w);
    }

    public String renderStr(String template, Object... args) {
        return renderString(template, args);
    }
    
    public String substitute(String template, Object... args) {
        ITemplate t = getTemplate(BasicRythm.INSTANCE, template, args);
        return t.render();
    }
    
    public String substitute(File template, Object... args) {
        ITemplate t = getTemplate(template, args, BasicRythm.INSTANCE);
        return t.render();
    }

    public String toString(String template, Object obj) {
        Class argClass = obj.getClass();
        String key = template + argClass;
        TemplateClass tc = classes.getByTemplate(key);
        if (null == tc) {
            tc = new TemplateClass(new StringTemplateResource(template), this, new ToString(argClass));
            classes.tmplIdx.put(key, tc);
        }
        ITemplate t = tc.asTemplate();
        t.setRenderArg(0, obj);
        return t.render();
    }

    public String toString(Object obj) {
        return toString(obj, ToStringOption.defaultOption, (ToStringStyle) null);
    }

    public String toString(Object obj, ToStringOption option, ToStringStyle style) {
        Class<?> c = obj.getClass();
        AutoToString.AutoToStringData key = new AutoToString.AutoToStringData(c, option, style);
        //String template = AutoToString.templateStr(c, option, style);
        TemplateClass tc = classes.getByTemplate(key);
        if (null == tc) {
            tc = new TemplateClass(new ToStringTemplateResource(key), this, new AutoToString(c, key));
            classes.tmplIdx.put(key, tc);
        }
        ITemplate t = tc.asTemplate();
        t.setRenderArg(0, obj);
        return t.render();
    }

    public String commonsToString(Object obj, ToStringOption option, org.apache.commons.lang3.builder.ToStringStyle style) {
        return toString(obj, option, ToStringStyle.fromApacheStyle(style));
    }

    @SuppressWarnings("unchecked")
    public String renderString(String template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);

        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(new StringTemplateResource(template), this);
        }
        ITemplate t = tc.asTemplate();
        setRenderArgs(t, args);
        return t.render();
    }

    public String render(File file, Object... args) {
        ITemplate t = getTemplate(file, args);
        return t.render();
    }

    public void render(OutputStream os, File file, Object... args) {
        outputMode.set(OutputMode.os);
        ITemplate t = getTemplate(file, args);
        t.render(os);
    }

    public void render(Writer w, File file, Object... args) {
        outputMode.set(OutputMode.writer);
        ITemplate t = getTemplate(file, args);
        t.render(w);
    }

    public Set<String> nonExistsTemplates = new HashSet<String>();

    private class NonExistsTemplatesChecker {
        boolean started = false;
        private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        NonExistsTemplatesChecker() {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    List<String> toBeRemoved = new ArrayList<String>();
                    for (String template : nonExistsTemplates) {
                        ITemplateResource rsrc = resourceManager.getFileResource(template);
                        if (rsrc.isValid()) {
                            toBeRemoved.add(template);
                        }
                    }
                    nonExistsTemplates.removeAll(toBeRemoved);
                    toBeRemoved.clear();
                    TemplateClass tc = classes.all().get(0);
                    for (String tag : nonExistsTags) {
                        if (null != resourceManager.tryLoadTag(tag, tc)) {
                            toBeRemoved.add(tag);
                        }
                    }
                    nonExistsTags.removeAll(toBeRemoved);
                    toBeRemoved.clear();
                }
            }, 0, 1000 * 10, TimeUnit.MILLISECONDS);
        }
    }

    private NonExistsTemplatesChecker nonExistsTemplatesChecker = null;

    public String renderIfTemplateExists(String template, Object... args) {
        ParamTypeInferencer.registerParams(this, args);
    
        if (nonExistsTemplates.contains(template)) return "";
        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            ITemplateResource rsrc = resourceManager.getFileResource(template);
            if (rsrc.isValid()) {
                tc = new TemplateClass(rsrc, this);
            } else {
                nonExistsTemplates.add(template);
                if (mode.isDev() && nonExistsTemplatesChecker == null) {
                    nonExistsTemplatesChecker = new NonExistsTemplatesChecker();
                }
                return "";
            }
        }
        ITemplate t = tc.asTemplate();
        setRenderArgs(t, args);
        return t.render();
    }

    private List<IJavaExtension> javaExtensions = new ArrayList<IJavaExtension>();
    
    public Iterable<IJavaExtension> javaExtensions() {
        return javaExtensions;
    }
    
    public boolean isJavaExtension(String methodName) {
        for (IJavaExtension ext : javaExtensions) {
            if (S.isEqual(methodName, ext.methodName())) {
                return true;
            }
        }
        return false;
    }

    // -- register java extension
    public void registerJavaExtension(IJavaExtension extension) {
        javaExtensions.add(extension);
    }

    /**
     * Register application Java extensions
     * 
     * @param extensionClass
     */
    public void registerJavaExtension(Class<?> extensionClass) {
        boolean classAnnotated = extensionClass.getAnnotation(Transformer.class) != null;
        for (Method m : extensionClass.getDeclaredMethods()) {
            int flag = m.getModifiers();
            if (!Modifier.isPublic(flag) || !Modifier.isStatic(flag)) continue;
            int len = m.getParameterTypes().length;
            if (len <= 0) continue;
            
            if (!classAnnotated) {
                boolean methodAnnotated = m.getAnnotation(Transformer.class) != null;
                if (!methodAnnotated) continue;
            }

            String cn = extensionClass.getSimpleName();
            String cn0 = extensionClass.getName();
            String mn = m.getName();
            if (len == 1) {
                registerJavaExtension(new IJavaExtension.VoidParameterExtension(cn, mn, String.format("%s.%s", cn0, mn)));
            } else {
                registerJavaExtension(new IJavaExtension.ParameterExtension(cn, mn, ".+", String.format("%s.%s", cn0, mn)));
            }
        }
    }


    public static void registerGlobalImportProvider(ISourceCodeEnhancer provider) {
        CodeBuilder.registerImportProvider(provider);
    }

    // -- tag relevant codes

    public final Map<String, ITag> tags = new HashMap<String, ITag>();
    public final Set<String> non_tags = new HashSet<String>();

    public TemplateClass getTemplateClassFromTagName(String name) {
        TemplateBase tag = (TemplateBase) tags.get(name);
        if (null == tag) return null;
        return tag.getTemplateClass(false);
    }

    public String testTag(String name, TemplateClass tc) {
        if (Keyword.THIS.toString().equals(name)) {
            return resourceManager.getFullTagName(tc);
        }
        if (mode.isProd() && non_tags.contains(name)) return null;
        boolean isTag = tags.containsKey(name);
        if (isTag) return name;
        // try imported path
        if (null != tc.importPaths) {
            for (String s : tc.importPaths) {
                String name0 = s + "." + name;
                if (tags.containsKey(name0)) return name0;
            }
        }
        // try relative path
        String callerName = resourceManager.getFullTagName(tc);
        int pos = callerName.lastIndexOf(".");
        if (-1 != pos) {
            String name0 = callerName.substring(0, pos) + "." + name;
            if (tags.containsKey(name0)) return name0;
        }

        try {
            // try to ask resource manager
            TemplateClass tagTC = resourceManager.tryLoadTag(name, tc);
            if (null == tagTC) {
                if (mode.isProd()) non_tags.add(name);
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
     *
     * @return true if registration failed
     */
    public boolean registerTag(ITag tag) {
        String name = tag.getName();
        return registerTag(name, tag);
    }

    /**
     * Register a tag using the given name
     *
     * @param name
     * @param tag
     * @return
     */
    public boolean registerTag(String name, ITag tag) {
        if (null == tag) throw new NullPointerException();
        if (tags.containsKey(name)) {
            return false;
        }
        tags.put(name, tag);
        logger.trace("tag %s registered", name);
        return true;
    }

    public void invokeTag(int line, String name, ITemplate caller, ITag.ParameterList params, ITag.Body body, ITag.Body context) {
        invokeTag(line, name, caller, params, body, context, false);
    }

    public Set<String> nonExistsTags = new HashSet<String>();

    public void invokeTag(int line, String name, ITemplate caller, ITag.ParameterList params, ITag.Body body, ITag.Body context, boolean ignoreNonExistsTag) {
        if (nonExistsTags.contains(name)) return;
        // try tag registry first
        ITag tag = tags.get(name);
        TemplateClass tc = ((TemplateBase) caller).getTemplateClass(true);
        if (null == tag) {
            // is calling self
            if (S.isEqual(name, ((TagBase)caller).getName())) tag = (TagBase)caller;
        }

        if (null == tag) {
            // try imported path
            if (null != tc.importPaths) {
                for (String s : tc.importPaths) {
                    String name0 = s + "." + name;
                    tag = tags.get(name0);
                    if (null != tag) break;
                }
            }

            // try relative path
            if (null == tag) {
                String callerName = resourceManager.getFullTagName(tc);
                int pos = callerName.lastIndexOf(".");
                if (-1 != pos) {
                    String name0 = callerName.substring(0, pos) + "." + name;
                    tag = tags.get(name0);
                }
            }

            // try load the tag from resource
            if (null == tag) {
                TemplateClass tagC = resourceManager.tryLoadTag(name, tc);
                if (null != tagC) tag = tags.get(tagC.getFullName());
                if (null == tag) {
                    if (ignoreNonExistsTag) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("cannot find tag: " + name);
                        }
                        nonExistsTags.add(name);
                        if (mode.isDev() && nonExistsTemplatesChecker == null) {
                            nonExistsTemplatesChecker = new NonExistsTemplatesChecker();
                        }
                        return;
                    } else  {
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
        RythmEvents.ON_TAG_INVOCATION.trigger(this, F.T2((TemplateBase)caller, tag));
        try {
            if (null != context) {
                ((TagBase)tag).setBodyContext(context);
            }
            tag.call(line);
        } finally {
            RythmEvents.TAG_INVOKED.trigger(this, F.T2((TemplateBase)caller, tag));
        }
    }


    public void handleTemplateExecutionException(Exception e, TemplateBase template) throws Exception {
        RythmEvents.ON_RENDER_EXCEPTION.trigger(this, F.T2(template, e));
        for (IRenderExceptionHandler h : em_.exceptionHandlers()) {
            if (h.handleTemplateExecutionException(e, template)) return;
        }
        throw e;
    }

    // -- cache api

    private Boolean _cacheOnProdOnly = null;
    private boolean cacheOnProdOnly() {
        if (null == _cacheOnProdOnly) {
            _cacheOnProdOnly = conf().get(RythmConfigurationKey.CACHE_PROD_ONLY_ENABLED);
        }
        return _cacheOnProdOnly;
    }

    /**
     * Default Time to live for cache items
     */
    private ICacheService _cacheService = null;

    /**
     * Return {@link ICacheService cache service implementation}
     * 
     * <p><b>Note</b>, this is not an API for user application</p>
     * 
     * @return
     */
    public ICacheService cacheService() {
        if (_cacheService == null) {
            _cacheService = conf().get(RythmConfigurationKey.CACHE_SERVICE_IMPL);
        }
        return _cacheService;
    }
    public IDurationParser durationParser = null;

    /**
     * Cache object using key and args for ttl seconds
     *
     * @param key
     * @param o
     * @param ttl  if zero then defaultTTL used, if negative then never expire
     * @param args
     */
    public void cache(String key, Object o, int ttl, Object... args) {
        if (conf().cacheDisabled()) return;
        ICacheService cacheService = cacheService();
        Serializable value = null == o ? "" : (o instanceof Serializable ? (Serializable)o : o.toString());
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
     *
     * @param key
     * @param o
     * @param duration
     * @param args
     */
    public void cache(String key, Object o, String duration, Object... args) {
        if (conf().cacheDisabled()) return;
        ICacheService cacheService = cacheService();
        int ttl = null == duration ? 0 : durationParser.parseDuration(duration);
        cache(key, o, ttl, args);
    }

    /**
     * Get cached value using key and a list of argument values
     *
     * @param key
     * @param args
     * @return
     */
    public Serializable cached(String key, Object... args) {
        if (conf().cacheDisabled()) return null;
        ICacheService cacheService = cacheService();
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
    private DialectManager dm_ = new DialectManager();

    public DialectManager getDialectManager() {
        return dm_;
    }

    private ExtensionManager em_ = new ExtensionManager(this);

    public ExtensionManager getExtensionManager() {
        return em_;
    }

    // -- issue #47
    private Map<TemplateClass, Set<TemplateClass>> extendMap = new HashMap<TemplateClass, Set<TemplateClass>>();
    public void addExtendRelationship(TemplateClass parent, TemplateClass child) {
        if (mode().isProd()) return;
        Set<TemplateClass> children = extendMap.get(parent);
        if (null == children) {
            children = new HashSet<TemplateClass>();
            extendMap.put(parent, children);
        }
        children.add(child);
    }
    // called to invalidate all template class which extends the parent
    public void invalidate(TemplateClass parent) {
        if (mode().isProd()) return;
        Set<TemplateClass> children = extendMap.get(parent);
        if (null == children) return;
        for (TemplateClass child: children) {
            invalidate(child);
            child.reset();
        }
    }

    // -- Sandbox
    
    private SandboxExecutingService _secureExecutor = null;
    private SandboxExecutingService secureExecutor() {
        if (null == _secureExecutor) {
            int poolSize = conf().get(RythmConfigurationKey.SANDBOX_POOL_SIZE);
            SecurityManager sm = conf().get(RythmConfigurationKey.SANDBOX_SECURITY_MANAGER_IMPL); 
            int timeout = conf().get(RythmConfigurationKey.SANDBOX_TIMEOUT);
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

    @Override
    public void accept(IEvent event, Object param) {
        eventDispatcher().accept(event, param);
    }
    
    // -- Shutdown
    
    interface IShutdownListener {
        void onShutdown();
    }
    private IShutdownListener shutdownListener = null;
    void setShutdownListener(IShutdownListener listener) {
        this.shutdownListener = listener; 
    }
    public void shutdown() {
        if (null != _cacheService) _cacheService.shutdown();
        if (null != _secureExecutor) _secureExecutor.shutdown();
        if (null != shutdownListener) shutdownListener.onShutdown();
    }

}
