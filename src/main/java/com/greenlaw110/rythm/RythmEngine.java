package com.greenlaw110.rythm;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.compiler.TemplateClassCache;
import com.greenlaw110.rythm.internal.compiler.TemplateClassLoader;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.ILoggerFactory;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.FileTemplateResource;
import com.greenlaw110.rythm.resource.TemplateResourceManager;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.spi.ExtensionManager;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.util.IO;
import com.greenlaw110.rythm.util.IRythmListener;
import com.greenlaw110.rythm.util.RythmProperties;

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
    public TemplateClassLoader classLoader = null;
    public IByteCodeHelper byteCodeHelper = null;
    public IHotswapAgent hotswapAgent = null;
    public Map<String, ?> defaultRenderArgs = null;

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
        mode = configuration.getAsMode("rythm.mode", Rythm.Mode.dev);
        
        cl = configuration.getAs("rythm.classLoader.parent", cl, ClassLoader.class);
        classLoader = new TemplateClassLoader(cl, this);
        defaultRenderArgs = configuration.getAs("rythm.defaultRenderArgs", null, Map.class);
        byteCodeHelper = configuration.getAs("rythm.classLoader.byteCodeHelper", null, IByteCodeHelper.class);
        hotswapAgent = configuration.getAs("rythm.classLoader.hotswapAgent", null, IHotswapAgent.class);


        if (null != tagHome) {
            loadTags();
        }
    }
    
    private void loadTags() {
        // code come from http://vafer.org/blog/20071112204524/
        class FileTraversal {
            public final void traverse( final File f )  {
                if (f.isDirectory()) {
                    onDirectory(f);
                    final File[] childs = f.listFiles();
                    for( File child : childs ) {
                        traverse(child);
                    }
                    return;
                }
                onFile(f);
            }
            public void onDirectory( final File d ) {
            }
            public void onFile( final File f ) {
            }
        }
        new FileTraversal(){
            @Override
            public void onFile(File f) {
                if (tagFileFilter.accept(f)) {
                    ITag tag = (ITag)getTemplate(f);
                    if (null != tag) registerTag(tag);
                }
            }
        }.traverse(tagHome);
    }

    ITemplate getTemplate(File template, Object... args) {
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
    
    ITemplate getTemplate(StringBuilder out, String template, Object... args) {
        TemplateClass tc = classes.getByTemplate(template);
        if (null == tc) {
            tc = new TemplateClass(template, this);
        }
        ITemplate t = tc.asTemplate(out);
        if (1 == args.length && args[0] instanceof Map) {
            t.setRenderArgs((Map<String, Object>)args[0]);
        } else {
            t.setRenderArgs(args);
        }
        return t;
    }

    ITemplate getTemplate(String template, Object... args) {
        return getTemplate(null, template, args);
    }
    
    private String renderTemplate(ITemplate t) {
        for (IRythmListener l: listeners) {
            l.onRender(t);
        }
        return t.render();
    }

    public String render(String template, Object... args) {
        ITemplate t = getTemplate(template, args);
        return renderTemplate(t);
    }

    public String render(File file, Object... args) {
        ITemplate t = getTemplate(file, args);
        return renderTemplate(t);
    }
    
    // -- tag relevant codes
    
    public final Map<String, ITag> tags = new HashMap<String, ITag>();

    /**
     * Register a tag class. If there is name collision then registration
     * will fail
     * @return true if registration failed
     */
    public boolean registerTag(ITag tag) {
        String name = tag.getName();
        if (tags.containsKey(name)) {
            return false;
        }
        tags.put(name, tag);
        return true;
    }

    public void invokeTag(String name, StringBuilder out, Object ... args) {
        // try tag registry first
        ITemplate tmpl = tags.get(name);
        if (null == tmpl) {
            // try tmpl file
            tmpl = getTemplate(out, name, args);
        } else {
            tmpl = tmpl.cloneMe(this, out);
            if (1 == args.length && args[0] instanceof Map) {
                tmpl.setRenderArgs((Map<String, Object>)args[0]);
            } else {
                tmpl.setRenderArgs(args);
            }
        }
        tmpl.render();
    }

    // -- SPI interface
    private DialectManager dm_ = new DialectManager();
    public DialectManager getDialectManager() {
        return dm_;
    }

    private ExtensionManager em_ = new ExtensionManager();
    public ExtensionManager getExtensionManager() {
        return em_;
    }
}
