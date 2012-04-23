package com.greenlaw110.rythm;

import com.greenlaw110.rythm.cache.ICacheService;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.exception.TagLoadException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.compiler.*;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.ILoggerFactory;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.ITemplateResourceLoader;
import com.greenlaw110.rythm.resource.StringTemplateResource;
import com.greenlaw110.rythm.resource.TemplateResourceManager;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.spi.ITemplateExecutionExceptionHandler;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.JavaTagBase;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.*;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class RythmEngine {

    Rythm.ReloadMethod reloadMethod = Rythm.ReloadMethod.RESTART;

    public boolean reloadByRestart() {
        return isDevMode() && reloadMethod == Rythm.ReloadMethod.RESTART;
    }

    public boolean reloadByIncClassVersion() {
        return isDevMode() && (reloadMethod == Rythm.ReloadMethod.V_VERSION);
    }

    public boolean classCacheEnabled() {
        return reloadByRestart();
    }

    public static final String version = "0.9.3";
    public static String pluginVersion = "";

    public static String versionSignature() {
        return version + "-" + pluginVersion;
    }

    private final ILogger logger = Logger.get(RythmEngine.class);
    public Rythm.Mode mode;
    public final RythmProperties configuration = new RythmProperties();
    public final TemplateResourceManager resourceManager = new TemplateResourceManager(this);
    public final TemplateClassManager classes = new TemplateClassManager(this);
    public TemplateClassLoader classLoader = null;
    public TemplateClassCache classCache = new TemplateClassCache(this);
    public IByteCodeHelper byteCodeHelper = null;
    public IHotswapAgent hotswapAgent = null;
    public boolean logRenderTime = false;
    public IImplicitRenderArgProvider implicitRenderArgProvider = null;
    /**
     * If this is set to true then @cacheFor() {} only effective on product mode
     */
    public boolean cacheOnProdOnly = true;
    /**
     * Default Time to live for cache items
     */
    public int defaultTTL = 60 * 60;
    public ICacheService cacheService = null;
    public IDurationParser durationParser = null;

    /**
     * Enable refresh resource on render. This could be turned off
     * if the resource reload service is managed by container, e.g. Play!framework
     */
    private boolean refreshOnRender = true;

    public boolean refreshOnRender() {
        return refreshOnRender && !isProdMode();
    }

    /**
     * When compactMode is true, then by default redundant spaces/line breaks are removed
     */
    private boolean compactMode = true;

    public boolean compactMode() {
        return compactMode;
    }

    /**
     * enable java extensions to expressions, e.g. @myvar.escapeHtml() or @myvar.pad(5) etc.
     * <p/>
     * disable java extension can improve parse performance
     */
    private boolean enableJavaExtensions = true;

    public boolean enableJavaExtensions() {
        return enableJavaExtensions;
    }

    public File tmpDir;
    public File templateHome;
    public File tagHome;
    public FileFilter tagFileFilter;

    public final List<IRythmListener> listeners = new ArrayList<IRythmListener>();

    public void registerListener(IRythmListener listener) {
        if (null == listener) throw new NullPointerException();
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void unregisterListener(IRythmListener listener) {
        if (null == listener) throw new NullPointerException();
        listeners.remove(listener);
    }

    public void clearListener() {
        listeners.clear();
    }

    public final List<ITemplateClassEnhancer> templateClassEnhancers = new ArrayList<ITemplateClassEnhancer>();

    public void registerTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        if (null == enhancer) throw new NullPointerException();
        if (!templateClassEnhancers.contains(enhancer)) templateClassEnhancers.add(enhancer);
    }

    public void unregisterTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        if (null == enhancer) throw new NullPointerException();
        templateClassEnhancers.remove(enhancer);
    }

    public void clearTemplateClassEnhancer() {
        templateClassEnhancers.clear();
    }

    public RythmEngine(File templateHome) {
        this(templateHome, null);
    }

    public RythmEngine(File templateHome, File tagHome) {
        init();
        this.templateHome = templateHome;
        this.tagHome = tagHome;
    }

    public RythmEngine(Properties userConfiguration) {
        init(userConfiguration);
    }

    public RythmEngine() {
        init();
    }

    public boolean isSingleton() {
        return Rythm.engine == this;
    }

    public boolean isProdMode() {
        return mode == Rythm.Mode.prod;
    }

    public boolean isDevMode() {
        return mode != Rythm.Mode.prod;
    }

    private void setConf(String key, Object val) {
        configuration.put(key, val);
    }

    private void loadDefConf() {
        setConf("rythm.mode", Rythm.Mode.prod);
        setConf("rythm.loader", "file");
        setConf("rythm.logJavaSource", false);
    }

    public void init() {
        init(null);
    }

    public void init(Properties conf) {
        loadDefConf();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) cl = Rythm.class.getClassLoader();
        URL url = cl.getResource("rythm.conf");
        if (null != url) {
            InputStream is = null;
            try {
                is = url.openStream();
                configuration.load(is);
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

        if (null != conf) configuration.putAll(conf);

        ILoggerFactory fact = configuration.getAs("rythm.logger.factory", null, ILoggerFactory.class);
        if (null != fact) Logger.registerLoggerFactory(fact);

        pluginVersion = configuration.getProperty("rythm.pluginVersion", "");
        refreshOnRender = configuration.getAsBoolean("rythm.resource.refreshOnRender", true);
        enableJavaExtensions = configuration.getAsBoolean("rythm.enableJavaExtensions", false);
        tmpDir = configuration.getAsFile("rythm.tmpDir", IO.tmpDir());
        // if templateHome set to null then it assumes use ClasspathTemplateResource by default
        templateHome = configuration.getAsFile("rythm.root", null);
        tagHome = configuration.getAsFile("rythm.tag.root", null);
        tagFileFilter = configuration.getAsFileFilter("rythm.tag.fileNameFilter", new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true; // by default accept all files
            }
        });
        mode = configuration.getAsMode("rythm.mode", Rythm.Mode.prod);
        compactMode = configuration.getAsBoolean("rythm.compactOutput", isProdMode());
        reloadMethod = configuration.getAsReloadMethod("rythm.reloadMethod", Rythm.ReloadMethod.RESTART);
        logRenderTime = configuration.getAsBoolean("rythm.logRenderTime", isDevMode());
        if (Rythm.ReloadMethod.V_VERSION == reloadMethod) {
            logger.warn("Rythm reload method set to increment class version, this will cause template class cache disabled.");
        }

        defaultTTL = configuration.getAsInt("rythm.cache.defaultTTL", 60 * 60);
        cacheService = configuration.getAsCacheService("rythm.cache.service");
        cacheService.setDefaultTTL(defaultTTL);
        durationParser = configuration.getAsDurationParser("rythm.cache.durationParser");

        cacheOnProdOnly = configuration.getAsBoolean("rythm.cache.prodOnly", true);

        cl = configuration.getAs("rythm.classLoader.parent", cl, ClassLoader.class);
        classLoader = new TemplateClassLoader(cl, this);
        classes.clear();
        tags.clear();
        //defaultRenderArgs = configuration.getAs("rythm.defaultRenderArgs", null, Map.class);
        implicitRenderArgProvider = configuration.getAs("rythm.implicitRenderArgProvider", null, IImplicitRenderArgProvider.class);
        byteCodeHelper = configuration.getAs("rythm.classLoader.byteCodeHelper", null, IByteCodeHelper.class);
        hotswapAgent = configuration.getAs("rythm.classLoader.hotswapAgent", null, IHotswapAgent.class);

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

        if (null != tagHome && configuration.getAsBoolean("rythm.tag.autoscan", true)) {
            loadTags();
        }
    }

    public void restart(RuntimeException cause) {
        if (isProdMode()) throw cause;
        if (!(cause instanceof ClassReloadException)) {
            logger.warn(cause, "restarting rythm engine due to %s", cause.getMessage());
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

    public void loadTags(File tagHome) {
        tags.clear();
        // code come from http://vafer.org/blog/20071112204524/
        class FileTraversal {
            public final void traverse(final File f) {
                if (f.isDirectory()) {
                    // aha, we don't want to traverse .svn
                    if (".svn".equals(f.getName())) return;
                    onDirectory(f);
                    final File[] childs = f.listFiles();
                    for (File child : childs) {
                        traverse(child);
                    }
                    return;
                }
                onFile(f);
            }

            public void onDirectory(final File d) {
            }

            public void onFile(final File f) {
                if (tagFileFilter.accept(f)) {
                    ITag tag = (ITag) getTemplate(f);
                    if (null != tag) registerTag(tag);
                }
            }
        }
        new FileTraversal().traverse(tagHome);
        logger.info("tags loaded from %s", tagHome);
    }

    private void loadTags() {
        loadTags(tagHome);
    }

    @SuppressWarnings("unchecked")
    public ITemplate getTemplate(File template, Object... args) {
        TemplateClass tc = classes.getByTemplate(resourceManager.get(template).getKey());
        if (null == tc) {
            tc = new TemplateClass(template, this);
        }
        ITemplate t = tc.asTemplate();
        if (null == t) return null;
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>) args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public ITemplate getTemplate(String template, Object... args) {
        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(template, this);
        }
        ITemplate t = tc.asTemplate();
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>) args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t;
    }

    public void preprocess(ITemplate t) {
        IImplicitRenderArgProvider p = implicitRenderArgProvider;
        if (null != p) p.setRenderArgs(t);
        for (IRythmListener l : listeners) {
            l.onRender(t);
        }
    }

    private String renderTemplate(ITemplate t) {
        if (null == t) return "This is not rythm template";
        // inject implicity render args
        return t.render();
    }

    public String render(String template, Object... args) {
        ITemplate t = getTemplate(template, args);
        return renderTemplate(t);
    }

    public String renderStr(String template, Object... args) {
        return renderString(template, args);
    }

    @SuppressWarnings("unchecked")
    public String renderString(String template, Object... args) {
        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(new StringTemplateResource(template), this);
        }
        ITemplate t = tc.asTemplate();
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>) args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t.render();
    }

    public String render(File file, Object... args) {
        ITemplate t = getTemplate(file, args);
        return renderTemplate(t);
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
        if (mode.isDev() && nonExistsTemplatesChecker == null) {
            nonExistsTemplatesChecker = new NonExistsTemplatesChecker();
        }
        if (nonExistsTemplates.contains(template)) return "";
        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            ITemplateResource rsrc = resourceManager.getFileResource(template);
            if (rsrc.isValid()) {
                tc = new TemplateClass(rsrc, this);
            } else {
                nonExistsTemplates.add(template);
                return "";
            }
        }
        ITemplate t = tc.asTemplate();
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>) args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t.render();
    }

    // -- register java extension
    public static void registerJavaExtension(IJavaExtension extension) {
        Token.addExtension(extension);
    }

    public static void registerGlobalImports(String imports) {
        CodeBuilder.registerImports(imports);
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
            String fullName = resourceManager.getFullTagName(tagTC);
            if (!S.isEqual(fullName, name)) {
                tags.put(fullName, tags.get(name));
            }
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
        if (tags.containsKey(name)) {
            return false;
        }
        tags.put(name, tag);
        logger.trace("tag %s registered", name);
        return true;
    }

    public void invokeTag(String name, ITemplate caller, ITag.ParameterList params, ITag.Body body) {
        invokeTag(name, caller, params, body, false);
    }

    public Set<String> nonExistsTags = new HashSet<String>();

    public void invokeTag(String name, ITemplate caller, ITag.ParameterList params, ITag.Body body, boolean ignoreNonExistsTag) {
        if (nonExistsTags.contains(name)) return;
        // try tag registry first
        ITag tag = tags.get(name);
        TemplateClass tc = ((TemplateBase) caller).getTemplateClass(true);
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
                resourceManager.tryLoadTag(name, tc);
                tag = tags.get(name);
                if (null == tag) {
                    if (ignoreNonExistsTag) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("cannot find tag: " + name);
                        }
                        nonExistsTags.add(name);
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
            if (reloadByIncClassVersion() && -1 == cn.indexOf("$")) {
                int pos = cn.lastIndexOf("v");
                if (-1 < pos) cn = cn.substring(0, pos);
            }
            TemplateClass tc0 = classes.getByClassName(cn);
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
        tag.call();
    }


    public void handleTemplateExecutionException(Exception e, TemplateBase template) throws Exception {
        for (ITemplateExecutionExceptionHandler h : em_.exceptionHandlers()) {
            if (h.handleTemplateExecutionException(e, template)) return;
        }
        throw e;
    }

    // -- cache api

    /**
     * Cache object using key and args for ttl seconds
     *
     * @param key
     * @param o
     * @param ttl  if zero then defaultTTL used, if negative then never expire
     * @param args
     */
    public void cache(String key, Object o, int ttl, Object... args) {
        if (mode.isDev() && cacheOnProdOnly) return;
        String value = null == o ? "" : o.toString();
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
        int ttl = null == duration ? defaultTTL : durationParser.parseDuration(duration);
        cache(key, o, ttl, args);
    }

    /**
     * Get cached value using key and a list of argument values
     *
     * @param key
     * @param args
     * @return
     */
    public String cached(String key, Object... args) {
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
}
