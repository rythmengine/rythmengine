package com.greenlaw110.rythm;

import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.compiler.TemplateClassCache;
import com.greenlaw110.rythm.internal.compiler.TemplateClassLoader;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.TemplateResourceManager;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.util.IO;
import com.greenlaw110.rythm.util.RythmProperties;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class RythmEngine {
    
    public static final String version = "0.1";
    
    private final ILogger logger = Logger.get(RythmEngine.class);
    public Rythm.Mode mode;
    public final RythmProperties configuration = new RythmProperties();
    public final TemplateResourceManager resourceManager = new TemplateResourceManager(this);
    public final TemplateClassCache classes = new TemplateClassCache(this);
    public final TemplateClassLoader classLoader = new TemplateClassLoader(this);

    public File tmpDir;
    public File templateHome;

    public final List<ITemplateClassEnhancer> templateClassEnhancers = new ArrayList<ITemplateClassEnhancer>();
    public void registerTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        if (null == enhancer) throw new NullPointerException();
        templateClassEnhancers.add(enhancer);
    }
    public void unregisterTemplateClassEnhancer(ITemplateClassEnhancer enhancer) {
        if (null == enhancer) throw new NullPointerException();
        templateClassEnhancers.remove(enhancer);

    }
    public void clearTemplateClassEnhancer() {
        templateClassEnhancers.clear();
    }
    
    public RythmEngine(File templateHome) {
        init();
        this.templateHome = templateHome;
    }
    
    public RythmEngine(Properties userConfiguration) {
        init();
        configuration.putAll(userConfiguration);
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
    
    private void setConf(String key, Object val) {
        configuration.put(key, val);
    }
    
    private void loadDefConf() {
        setConf("rythm.mode", Rythm.Mode.dev);
        setConf("rythm.loader", "file");
    }

    public void init() {
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
        
        tmpDir = configuration.getAsFile("rythm.tmpDir", IO.tmpDir());
        // if templateHome set to null then it assumes use ClasspathTemplateResource by default
        templateHome = configuration.getAsFile("rythm.root", null);
        mode = configuration.getAsMode("rythm.mode", Rythm.Mode.dev);
    }

    private ITemplate getTemplate(File template, Object... args) {
        TemplateClass tc = classes.getByTemplate(resourceManager.get(template).getKey());
        if (null == tc) {
            tc = new TemplateClass(template, this);
        }
        ITemplate t = tc.asTemplate();
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>)args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t;
    }

    private ITemplate getTemplate(String template, Object... args) {
        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(template, this);
        }
        ITemplate t = tc.asTemplate();
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>)args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t;
    }

    public String render(String template, Object... args) {
        ITemplate t = getTemplate(template, args);
        return t.render();
    }

    public String render(File file, Object... args) {
        ITemplate t = getTemplate(file, args);
        return t.render();
    }

    // -- SPI interface
    private static DialectManager dm_ = new DialectManager();
    public static DialectManager getDialectManager() {
        return dm_;
    }

    private static ExtensionManager em_ = new ExtensionManager();
    public static ExtensionManager getExtensionManager() {
        return em_;
    }
}
