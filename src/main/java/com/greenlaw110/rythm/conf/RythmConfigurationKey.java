package com.greenlaw110.rythm.conf;

import com.greenlaw110.rythm.ILang;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.cache.NoCacheService;
import com.greenlaw110.rythm.cache.SimpleCacheService;
import com.greenlaw110.rythm.exception.ConfigurationException;
import com.greenlaw110.rythm.resource.ITemplateResourceLoader;
import com.greenlaw110.rythm.utils.IDurationParser;
import com.greenlaw110.rythm.utils.S;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link com.greenlaw110.rythm.RythmEngine} configuration keys. General rules:
 * 
 * <ul>
 * <li>When a key is ended with <code>.enabled</code>, then you should be able to set
 * the setting without <code>.enabled</code> or replace it with <code>.disabled</code>
 * but the value will be inverted. For example, <code>built_in.transformer.enabled</code>
 * is equal to <code>built_in.transformer</code> and invert to
 * <code>built_in.transformer.disabled</code></li>
 * <li>When a key is ended with <code>.impl</code>, then you can either put an instance into
 * the configuration map or a string of the class name</li>
 * </ul>
 */
public enum RythmConfigurationKey {

    /**
     * Enable built-in {@link com.greenlaw110.rythm.ILang template language} implementations
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    BUILT_IN_TEMPLATE_LANG_ENABLED("built_in.template_lang.enabled", true),

    /**
    * Enable built-in {@link com.greenlaw110.rythm.spi.Transformer transformer} implementations
    * <p/>
    * <p>Default value: <code>true</code></p>
    *
    * @see #FEATURE_TRANSFORMER_ENABLED
    */
    BUILT_IN_TRANSFORMER_ENABLED("built_in.transformer.enabled", true),

    /**
     * Enable disable {@link com.greenlaw110.rythm.cache.ICacheService cache service}. When this
     * setting is turned off, the {@link #CACHE_SERVICE_IMPL} will be set to
     * {@link com.greenlaw110.rythm.cache.NoCacheService} without regarding to it's configuration
     * <p/>
     * <p>Default value: <code>false</code></p>
     * <p/>
     * TODO: add link to cache service reference
     */
    CACHE_ENABLED("cache.enabled", false),

    /**
     * Set {@link com.greenlaw110.rythm.cache.ICacheService cache service} implementation
     * <p/>
     * <p>Default value: {@link com.greenlaw110.rythm.cache.SimpleCacheService}</p>
     * <p/>
     * <p>Note when {@link #CACHE_ENABLED} is set to <code>false</code>, then this setting
     * will be ignored, and the service impl will be set to {@link com.greenlaw110.rythm.cache.NoCacheService}
     * anyway</p>
     * <p/>
     * TODO: add link to cache service reference
     */
    CACHE_SERVICE_IMPL("cache.service.impl") {
        @Override
        protected Object getDefVal(Map<String, ?> configuration) {
            Boolean cacheEnabled = CACHE_ENABLED.getConfiguration(configuration);
            return cacheEnabled ? SimpleCacheService.INSTANCE : NoCacheService.INSTANCE;
        }
    },

    /**
     * Set {@link com.greenlaw110.rythm.utils.IDurationParser duration parser} implementation.
     * <p/>
     * <p>Default value: {@link com.greenlaw110.rythm.utils.IDurationParser#DEFAULT_PARSER}</p>
     */
    CACHE_DURATION_PARSER_IMPL("cache.duration_parser.impl", IDurationParser.DEFAULT_PARSER),

    /**
     * Turn on/off cache at {@link com.greenlaw110.rythm.Rythm.Mode#dev dev} mode. When
     * this setting is turned on, then cache will not effect at dev mode
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    CACHE_PROD_ONLY_ENABLED("cache.prod_only.enabled", true),

    /**
     * Enable/disable compact redundant space and lines
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    CODEGEN_COMPACT_ENABLED("codegen.compact.enabled", true),

    /**
     * Set {@link com.greenlaw110.rythm.utils.IImplicitRenderArgProvider implicit template argument provider}
     * implementation.
     * <p/>
     * <p>Default value: <code>null</code></p>
     */
    CODEGEN_IMPLICIT_ARG_PROVIDER("codegen.implict_arg_provider.impl"),

    /**
     * Set default {@link com.greenlaw110.rythm.ILang template lang}
     * <p/>
     * <p>Default value: {@link com.greenlaw110.rythm.ILang.DefImpl#HTML}</p>
     * <p/>
     * TODO: what if {@link #BUILT_IN_TEMPLATE_LANG_ENABLED} is false
     */
    DEFAULT_TEMPLATE_LANG_IMPL("default.template_lang.impl", ILang.DefImpl.HTML),

    /**
     * Set default {@link com.greenlaw110.rythm.cache.ICacheService cache} ttl
     * in second
     * <p/>
     * <p>Default value: 60 * 60(1hr</p>
     */
    DEFAULT_CACHE_TTL("default.cache_ttl") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            String k = getKey();
            Object v = configuration.get(k);
            if (null == v) {
                return (T) (Number) (60 * 60);
            }
            if (v instanceof Number) {
                return (T) v;
            }
            return (T) (Integer.valueOf(v.toString()));
        }
    },

    /**
     * Set the {@link com.greenlaw110.rythm.Rythm.Mode mode} of rythm engine
     * <p/>
     * <p>Default value: {@link com.greenlaw110.rythm.Rythm.Mode#prod}</p>
     */
    ENGINE_MODE("engine.mode") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            String k = getKey();
            Object v = configuration.get(k);
            if (null == v) {
                return (T) Rythm.Mode.prod;
            } else {
                if (v instanceof Rythm.Mode) {
                    return (T) v;
                } else {
                    return (T) Rythm.Mode.valueOf(v.toString());
                }
            }
        }
    },

    /**
     * Set the {@link ClassLoader#getParent() parent} class loader of the rythm template class loader
     * <p/>
     * <p>Default value: first try to use {@link Thread#getContextClassLoader() current thread's context class loader}
     * if the context classloader is <code>null</code>, then use the class loader which loads the <code>Rythm.class</code></p>
     */
    ENGINE_CLASS_LOADER_PARENT_IMPL("engine.class_loader.parent.impl") {
        @Override
        protected Object getDefVal(Map<String, ?> configuration) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (null == cl) {
                cl = Rythm.class.getClassLoader();
            }
            return cl;
        }
    },

    /**
     * Set the {@link com.greenlaw110.rythm.IByteCodeHelper bytecode helper} implementation
     * <p/>
     * <p>Default value: <code>null</code></p>
     */
    ENGINE_CLASS_LOADER_BYTECODE_HELPER_IMPL("engine.class_loader.bytecode_helper.impl"),

    /**
     * Set the {@link com.greenlaw110.rythm.IHotswapAgent hotswap agent} implementation
     * <p/>
     * <p>Default value: <code>null</code></p>
     */
    ENGINE_CLASS_LOADER_HOTSWAP_AGENT_IMPL("engine.class_loader.hotswap_agent.impl"),

    /**
     * Set the flag so that Rythm will load precompiled template class directly from
     * bytecode cached in the {@link #HOME_PRECOMPILED precompiled root} when running
     * in {@link com.greenlaw110.rythm.Rythm.Mode#prod prod} mode.
     * <p/>
     * <p>Default value: <code>false</code></p>
     */
    ENGINE_LOAD_PRECOMPILED_ENABLED("engine.load_precompiled.enabled", false),

    /**
     * Enable/disable write to file system. This option is used by rythm to check if
     * it should write template class bytecode cache to disk or not. In some cases 
     * you want to disable file write due the limit of the runtime environment, e.g.
     * on GAE platform
     * 
     * <p>Default value: <code>true</code></p>
     */
    ENGINE_FILE_WRITE_ENABLED("engine.file_write.enabled", true),

    /**
     * A special flag used when Rythm is working with rythm-plugin for Play!Framework. Usually
     * you should not touch this setting.
     * <p/>
     * <p>Default value: <code>false</code></p>
     */
    ENGINE_PLAYFRAMEWORK("engine.playframework.enabled", false),

    /**
     * Set by plugin of certain framework, e.g. play!framework. Used to determine whether it needs
     * to refresh the cached template class bytecode. Default value: <code>""</code> (empty string)
     */
    ENGINE_PLUGIN_VERSION("engine.plugin.version", ""),

    /**
     * Enable disable {@link com.greenlaw110.rythm.spi.Transformer transformer}
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    FEATURE_TRANSFORMER_ENABLED("feature.transformer.enabled", true),

    /**
     * Enable disable type inference. TODO add link to type inference reference page
     * <p/>
     * <p>Default value: <code>false</code></p>
     */
    FEATURE_TYPE_INFERENCE_ENABLED("feature.type_inference.enabled", false),

    /**
     * Enable disable smart escape. TODO: add link to smart escape reference page
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    FEATURE_SMART_ESCAPE_ENABLED("feature.smart_escape.enabled", true),

    /**
     * Enable disable natural template. TODO: add reference link to natural template
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    FEATURE_NATURAL_TEMPLATE_ENABLED("feature.natural_template.enabled", true),

    /**
     * Set the home dir of template files. This configuration is used when the {@link #RESOURCE_LOADER_IMPL}
     * is not configured, therefore the {@link com.greenlaw110.rythm.resource.TemplateResourceManager} will
     * try to load {@link com.greenlaw110.rythm.resource.FileTemplateResource} from this template home dir
     * configured.
     * <p/>
     * <p>Default value: a file created with the following logic</p>
     * <p/>
     * <pre><code>new File(URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(".").getFile(), "UTF-8"));</code></pre>
     */
    HOME_TEMPLATE("home.template") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            ITemplateResourceLoader loader = RESOURCE_LOADER_IMPL.getConfiguration(configuration);
            if (null != loader) {
                return null;
            }
            try {
                File f = getFile(getKey(), configuration);
                if (null == f) {
                    f = new File(URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(".").getFile(), "UTF-8"));
                }
                if (!f.exists()) {
                    throw new ConfigurationException("template root [%s] does not exists", f.getAbsolutePath());
                }
                if (!f.isDirectory()) {
                    throw new ConfigurationException("template root [%s] is not a directory", f.getAbsolutePath());
                }
                if (!f.canRead()) {
                    throw new ConfigurationException("template root [%s] is not readable", f.getAbsolutePath());
                }
                return (T) f;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    },

    /**
     * Set the rythm tmp dir. The tmp dir is to where Rythm write compiled template class bytecode when running in the
     * {@link com.greenlaw110.rythm.Rythm.Mode#dev dev} mode.
     * <p/>
     * <p>Default value: a file created with the following logic</p>
     * <p/>
     * <pre><code>new File(System.getProperty("java.io.tmpdir"), "__rythm")</code></pre>
     */
    HOME_TMP("home.tmp") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            Rythm.Mode mode = ENGINE_MODE.getConfiguration(configuration);
            if (mode.isProd()) {
                return null;
            }
            File f = getFile(getKey(), configuration);
            if (null == f) {
                f = new File(System.getProperty("java.io.tmpdir"), "__rythm");
            }
            if (!f.exists()) {
                if (!f.mkdirs()) {
                    throw new ConfigurationException("tmp dir [%s] cannot be created", f.getAbsolutePath());
                }
            }
            if (!f.isDirectory()) {
                throw new ConfigurationException("tmp dir [%s] is not a directory", f.getAbsolutePath());
            }
            if (!f.canRead()) {
                throw new ConfigurationException("tmp dir [%s] is not readable", f.getAbsolutePath());
            }
            if (!f.canWrite()) {
                throw new ConfigurationException("tmp dir [%s] is not writable", f.getAbsolutePath());
            }
            return (T) f;
        }
    },

    /**
     * Set the dir root of the precompiled template bytecodes. Default value: <code>null</code>
     *
     * @see #ENGINE_LOAD_PRECOMPILED_ENABLED
     */
    HOME_PRECOMPILED("home.precompiled") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            Rythm.Mode mode = ENGINE_MODE.getConfiguration(configuration);
            if (mode.isDev()) {
                return null;
            }
            File f = getFile(getKey(), configuration);
            if (null == f) return null;
            if (!f.exists()) {
                throw new ConfigurationException("precompiled dir [%s] does not exists", f.getAbsolutePath());
            }
            if (!f.isDirectory()) {
                throw new ConfigurationException("precompiled dir [%s] is not a directory", f.getAbsolutePath());
            }
            if (!f.canRead()) {
                throw new ConfigurationException("precompiled dir [%s] is not readable", f.getAbsolutePath());
            }
            return (T) f;
        }
    },

    /**
     * Enable disable log in Rythm. Default value: true
     */
    LOG_ENABLED("log.enabled", true),

    /**
     * Configure the {@link com.greenlaw110.rythm.logger.ILoggerFactory logger factory} implementation.
     * When this configuration is not set, then a {@link com.greenlaw110.rythm.logger.JDKLogger.Factory} instance
     * is used to create the logger
     * <p/>
     * <p>Default value: <code>null</code></p>
     */
    LOG_FACTORY_IMPL("log.factory.impl"),

    /**
     * Print out relevant java source lines when exception encountered
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    LOG_SOURCE_JAVA_ENABLED("log.source.java.enabled", true),

    /**
     * Print out relevant template source lines when exception encountered
     * <p/>
     * <p>Default value: <code>true</code></p>
     */
    LOG_SOURCE_TEMPLATE_ENABLED("log.source.template.enabled", true),

    /**
     * Log time spent executing a template. The level used to log the time logRenderTime
     * is {@link com.greenlaw110.rythm.logger.ILogger#debug(String, Object...)}
     * <p/>
     * <p>Default value: <code>false</code></p>
     */
    LOG_TIME_RENDER_ENABLED("log.time.render.enabled", false),

    /**
     * The {@link com.greenlaw110.rythm.resource.ITemplateResourceLoader resource loader} implementation
     * <p/>
     * <p>Default value: <code>null</code>. But if this is not configured, try templates will be loaded as
     * {@link com.greenlaw110.rythm.resource.FileTemplateResource file template resource} first and if
     * still not found then try to load as
     * {@link com.greenlaw110.rythm.resource.ClasspathTemplateResource classpath resource}.</p>
     *
     * @see #HOME_TEMPLATE
     */
    RESOURCE_LOADER_IMPL("resource.loader.impl"),

    /**
     * Set the security manager to be used when running a template in {@link com.greenlaw110.rythm.Sandbox sandbox}
     * mode.
     * <p/>
     * <p>Default value: <code>null</code>. When no security manager is configured, when the sandbox mode is running, an
     * instance of {@link com.greenlaw110.rythm.sandbox.RythmSecurityManager} will be initiated to supervise the
     * execution. Usually you should NOT set this configuration and allow Rythm to run it's <code>SecurityManager</code>
     * implementation</p>
     */
    SANDBOX_SECURITY_MANAGER_IMPL("sandbox.security_manager.impl"),

    /**
     * Set the timeout of a {@link com.greenlaw110.rythm.Sandbox sandbox} execution in milliseconds. If the execution
     * failed to return after timeout, then Rythm will interrupt the execution thread and force it to return. This
     * setting prevent infinite loop in untrusted template.
     * <p/>
     * <p>Default value: 1000</p>
     */
    SANDBOX_TIMEOUT("sandbox.timeout") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            String k = getKey();
            Object v = configuration.get(k);
            if (null == v) {
                return (T) (Integer) 1000;
            }
            if (v instanceof Number) {
                return (T) v;
            }
            return (T) Integer.valueOf(v.toString());
        }
    },

    /**
     * Set the thread pool size of {@link com.greenlaw110.rythm.Sandbox sandbox} executors.
     * <p/>
     * <p>Default value: 10</p>
     */
    SANDBOX_POOL_SIZE("sandbox.pool.size") {
        @Override
        public <T> T getConfiguration(Map<String, ?> configuration) {
            String k = getKey();
            Object v = configuration.get(k);
            if (null == v) {
                return (T) (Integer) 10;
            }
            if (v instanceof Number) {
                return (T) v;
            }
            return (T) Integer.valueOf(v.toString());
        }
    },

    /**
     * Set restricted classes for {@link com.greenlaw110.rythm.Sandbox sandbox} execution. The value should be
     * full name of the classes or packages separated by <code>;</code>. For example, "foo.bar.Employee;foo.secure;...".
     * If a class or package name is presented in this setting, then the sandbox executor will raise a
     * {@link SecurityException} when the template trying to access the class. Note whatever this setting is
     * configured, Rythm will prevent the access to the following classes/packages:
     * <p/>
     * <ul>
     * <li><code>com.greenlaw110.rythm.Rythm;</code></li>
     * <li><code>com.greenlaw110.rythm.RythmEngine;</code></li>
     * <li><code>java.io;</code></li>
     * <li><code>java.nio;</code></li>
     * <li><code>java.security;</code></li>
     * <li><code>java.rmi;</code></li>
     * <li><code>java.net;</code></li>
     * <li><code>java.awt;</code></li>
     * <li><code>java.applet</code></li>
     * </ul>
     * <p/>
     * <p/>
     * <code>Default value: <code>null</code></code>
     */
    SANDBOX_RESTRICTED_CLASS("sandbox.restricted_class");

    private String key;
    private Object defVal;

    private RythmConfigurationKey(String key) {
        this(key, null);
    }

    private RythmConfigurationKey(String key, Object defVal) {
        this.key = key;
        this.defVal = defVal;
    }

    /**
     * Return the key string 
     * 
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * Return default value of this setting. The configuration data map
     * is passed in in case the default value be variable depending on
     * another setting. For example, the default value of {@link #HOME_TMP tmp dir}
     * setting depend on the value of {@link #ENGINE_MODE mode} setting
     * 
     * @param configuration
     * @return
     */
    protected Object getDefVal(Map<String, ?> configuration) {
        return defVal;
    }

    /**
     * Calling to this method is equals to calling {@link #getKey()}
     * 
     * @return
     */
    @Override
    public String toString() {
        return key;
    }

    private static Boolean getAsBoolean(String key, Map<String, ?> configuration) {
        Object o = configuration.get(key);
        if (null == o) {
            return false;
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return Boolean.valueOf(o.toString());
        }
    }

    private Boolean getEnabled(String key, Map<String, ?> configuration) {
        String k0 = key.replace(".enabled", "");
        String k1 = k0 + ".disabled";
        if (configuration.containsKey(k1)) {
            return (!getAsBoolean(k1, configuration));
        }
        if (configuration.containsKey(key)) {
            return getAsBoolean(key, configuration);
        }
        if (configuration.containsKey(k0)) {
            return getAsBoolean(k0, configuration);
        }
        Object defVal = getDefVal(configuration);
        return (null == defVal) ? false : (Boolean) defVal;
    }

    private <T> T getImpl(String key, Map<String, ?> configuration) {
        Object v = configuration.get(key);
        if (null == v) {
            String k0 = key.replace(".impl", "");
            v = configuration.get(key);
            if (null == v) {
                v = getDefVal(configuration);
            }
        }
        if (null == v) {
            return null;
        }
        if (v instanceof String) {
            String clsName = (String) v;
            try {
                return (T) Class.forName(clsName).newInstance();
            } catch (Exception e) {
                throw new ConfigurationException(e, "Error getting implementation configuration: %s", key);
            }
        } else {
            return (T) v;
        }
    }

    private static File getFile(String key, Map<String, ?> configuration) {
        Object v = configuration.get(key);
        if (null == v) {
            return null;
        }
        if (v instanceof File) {
            return (File) v;
        }
        return new File(v.toString());
    }

    /**
     * Return configuration value from the configuration data map using the {@link #key}
     * of this {@link RythmConfigurationKey setting} instance
     *
     * @param configuration
     * @param <T>
     * @return
     */
    public <T> T getConfiguration(Map<String, ?> configuration) {
        String key = this.key;
        if (key.endsWith(".enabled")) {
            return (T) getEnabled(key, configuration);
        }
        if (key.endsWith(".impl")) {
            return getImpl(key, configuration);
        }
        Object o = configuration.get(key);
        if (null == o) {
            return (T) getDefVal(configuration);
        } else {
            return (T) o;
        }
    }
    
    private static Map<String, RythmConfigurationKey> lookup = new HashMap<String, RythmConfigurationKey>(50); static {
        for (RythmConfigurationKey k: values()) {
            lookup.put(k.getKey().toLowerCase(), k);
        }
    }

    public static RythmConfigurationKey valueOfIgnoreCase(String s) {
        if (S.empty(s)) throw new IllegalArgumentException();
        return lookup.get(s.trim());
    }

    public static void main(String[] args) {
        System.out.println(values().length);
    }
}
