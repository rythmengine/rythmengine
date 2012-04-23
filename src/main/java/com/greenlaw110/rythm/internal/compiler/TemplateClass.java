package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.CompileException;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.StringTemplateResource;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.S;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define the data structure hold template class/template src/generated java src
 */
public class TemplateClass {
    private static AtomicLong nextVersion = new AtomicLong();
    private static final ILogger logger = Logger.get(TemplateClass.class);

    /**
     * Store root level template class, e.g. the one that is not an embedded class
     */
    private TemplateClass root;
    public TemplateClass root() {
        return root;
    }

    private TemplateClass() {}
    private boolean inner = false;
    public static TemplateClass createInnerClass(String className, byte[] byteCode, TemplateClass parent) {
        TemplateClass tc = new TemplateClass();
        tc.name = className;
        tc.javaByteCode = byteCode;
        tc.enhancedByteCode = byteCode;
        tc.inner = true;
        tc.root = parent.root();
        tc.version = parent.version();
        return tc;
    }
    public boolean isInner() {return inner;}

    private RythmEngine engine = null;
    private RythmEngine engine() {
        return null == engine ? Rythm.engine : engine;
    }

    /**
     * The fully qualified class name
     */
    private String name;
    public String name0() {
        return name;
    }
    public String name() {
        //return isInner() ? name : name + "v" + version;
        RythmEngine e = engine();
        String n =  (!e.reloadByIncClassVersion() || isInner()) ? name : name + "v" + version;
        return n;
    }
    private long version;
    public long version() {
        return root().version;
    }
    public void setVersion(int v) {
        version = (long)v;
    }
    public TemplateClass extendedTemplateClass;
    private Set<TemplateClass> includedTemplateClasses = new HashSet<TemplateClass>();
    public void addIncludeTemplateClass(TemplateClass tc) {
        includedTemplateClasses.add(tc);
    }
    public String includeTemplateClassNames = null;
    private static final String NO_INCLUDE_CLASS = "NO_INCLUDE_CLASS";
    public String refreshIncludeTemplateClassNames() {
        if (includedTemplateClasses.size() == 0) {
            includeTemplateClassNames = NO_INCLUDE_CLASS;
            return NO_INCLUDE_CLASS;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (TemplateClass tc: includedTemplateClasses) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(engine().resourceManager.getFullTagName(tc));
        }
        includeTemplateClassNames = sb.toString();
        return sb.toString();
    }
    /**
     * If not null then this template is a tag
     */
    public String tagName() {
        return null != templateResource ? templateResource.tagName() : null;
    }
    /**
     * the template resource
     */
    public ITemplateResource templateResource;
    /**
     * The template source
     */
    public String getTemplateSource() {
        return getTemplateSource(false);
    }
    public String getTemplateSource(boolean includeRoot) {
        if (null != templateResource) return templateResource.asTemplateContent();
        if (!includeRoot) return "";
        TemplateClass parent = root;
        while ((null != parent) && parent.isInner()) {
            parent = parent.root;
        }
        return null == parent ? "" : parent.getTemplateSource();
    }
    /**
     * Is this template resource coming from a literal String or from a loaded resource like file
     */
    public boolean isStringTemplate() {
        return templateResource instanceof StringTemplateResource;
    }
    /**
     * The Java source
     */
    public String javaSource;
    /**
     * The compiled byteCode
     */
    public byte[] javaByteCode;
    /**
     * The enhanced byteCode
     */
    public byte[] enhancedByteCode;
    /**
     * Store a list of import path, i.e. those imports ends with ".*"
     */
    public Set<String> importPaths;
    /**
     * The in JVM loaded class
     */
    public Class<ITemplate> javaClass;
    /**
     * The in JVM loaded package
     */
    public Package javaPackage;
    /**
     * Is this class compiled
     */
    boolean compiled;
    /**
     * Signatures checksum
     */
    public int sigChecksum;

    /**
     * Mark if this is a valid Rythm Template
     */
    public boolean isValid = true;

    /**
     * CodeBuilder to generate java source code
     *
     * Could be used to merge state into including template class codeBuilder
     */
    public CodeBuilder codeBuilder;

    /**
     * The ITemplate instance
     */
    public TemplateBase templateInstance;

    private TemplateClass(RythmEngine engine) {
        this.engine = null == engine ? null : engine.isSingleton() ? null : engine;
    }

    /**
     * Construct a TemplateClass instance using template source file
     *
     * @param file the template source file
     */
    public TemplateClass(File file, RythmEngine engine) {
        this(engine.resourceManager.get(file), engine);
    }

    /**
     * Construct a TemplateClass instance using template source content or file path
     * @param template
     */
    public TemplateClass(String template, RythmEngine engine) {
        this(engine.resourceManager.get(template), engine);
    }

    public TemplateClass(ITemplateResource resource, RythmEngine engine) {
        this(resource, engine, false);
    }

    public TemplateClass(ITemplateResource resource, RythmEngine engine, boolean noRefresh) {
        this(engine);
        if (null == resource) throw new NullPointerException();
        templateResource = resource;
        if (!noRefresh) refresh();
    }


    /**
     * Return string representation of the template
     * @return
     */
    public String getKey() {
        return null == templateResource ? name() : templateResource.getKey();
    }

    @SuppressWarnings("unchecked")
    private Class<?> getJavaClass() throws Exception {
        Class<?> c = engine().classLoader.loadClass(name(), true);
        if (null == javaClass) javaClass = (Class<ITemplate>)c;
        return c;
    }

    private static final ITemplate NULL_TEMPLATE = new TemplateBase() {
        @Override
        public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
            return null;
        }
    };

    private ITemplate templateInstance_() {
        if (!isValid) return  NULL_TEMPLATE;
        if (null == templateInstance) {
            try {
                Class<?> clz = getJavaClass();
                templateInstance = (TemplateBase) clz.newInstance();
            } catch (RythmException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error load template instance for " + getKey(), e);
            }
        }
        if (!engine().isProdMode()) {
            // check parent class change
            Class<?> c = templateInstance.getClass();
            Class<?> pc = c.getSuperclass();
            if (null != pc && !Modifier.isAbstract(pc.getModifiers())) {
                engine().classes.getByClassName(pc.getName());
            }
        }
        templateInstance.setTemplateClass(this);
        return templateInstance;
    }

    public ITemplate asTemplate() {
        if (null == name) refresh();
        return templateInstance_().cloneMe(engine(), null);
    }

    public ITemplate asTemplate(ITemplate caller) {
        return templateInstance_().cloneMe(engine(), caller);
    }

    private boolean refreshing = false;
    private boolean compiling = false;
    private boolean refreshing() {
        return refreshing || compiling;
    }

    private void addVersion() {
        RythmEngine e = engine();
        if (!e.reloadByIncClassVersion()) return;
        TemplateClassManager tcc = engine().classes;
        tcc.clsNameIdx.remove(name());
        //List<TemplateClass> allEmbedded = tcc.getEmbeddedClasses(name0());
        version = nextVersion.getAndIncrement();
        tcc.clsNameIdx.put(name(), this);
    }

    public boolean refresh() {
        return refresh(false);
    }

    public void buildSourceCode() {
        long start = System.currentTimeMillis();
        addVersion();
        importPaths = new HashSet<String>();
        if (null != codeBuilder) codeBuilder.clear();
        codeBuilder = new CodeBuilder(templateResource.asTemplateContent(), name(), tagName(), this, engine);
        codeBuilder.build();
        extendedTemplateClass = codeBuilder.getExtendedTemplateClass();
        javaSource = codeBuilder.toString();
        if (logger.isTraceEnabled()) {
            logger.trace("%s ms to generate java source for template: %s", System.currentTimeMillis() - start, getKey());
        }
    }

    /**
     * @return true if this class has changes refreshed, otherwise this class has not been changed yet
     */
    public boolean  refresh(boolean forceRefresh) {
        if (refreshing()) return false;
        if (inner) return false;
        try {
            RythmEngine e = engine();
            refreshing = true;
            if (!templateResource.isValid()) {
                // it is removed?
                isValid = false;
                engine().classes.remove(this);
                return false;
            }
            if (null == name) {
                // this is the root level template class
                root = this;
                name = templateResource.getSuggestedClassName() + CN_SUFFIX;
                //name = templateResource.getSuggestedClassName();
                if (e.reloadByIncClassVersion()) version = nextVersion.getAndIncrement();
                engine().classes.add(this);
            }

            if (null == javaSource) {
                engine().classCache.loadTemplateClass(this);
                if (null != javaSource) {
                    // try refresh extended template class if there is
                    Pattern p = Pattern.compile(".*extends\\s+([a-zA-Z0-9_]+)\\s*\\{\\s*\\/\\/<extended_resource_key\\>(.*)\\<\\/extended_resource_key\\>.*", Pattern.DOTALL);
                    Matcher m = p.matcher(javaSource);
                    if (m.matches()) {
                        String extended = m.group(1);
                        TemplateClassManager tcm = engine().classes;
                        extendedTemplateClass = tcm.getByClassName(extended);
                        if (null == extendedTemplateClass) {
                            String extendedResourceKey = m.group(2);
                            extendedTemplateClass = tcm.getByTemplate(extendedResourceKey);
                            if (null == extendedTemplateClass) {
                                extendedTemplateClass = new TemplateClass(extendedResourceKey, engine());
                                extendedTemplateClass.refresh();
                            }
                        }
                    }
                }
            }

            boolean extendedTemplateChanged = false;
            if (extendedTemplateClass != null) extendedTemplateChanged = extendedTemplateClass.refresh(forceRefresh);
            boolean includedTemplateChanged = false;
            if (includedTemplateClasses.size() == 0 && !S.isEmpty(includeTemplateClassNames) && !NO_INCLUDE_CLASS.equals(includeTemplateClassNames)) {
                // just loaded from persistent store
                for (String tcName: includeTemplateClassNames.split(",")) {
                    if (S.isEmpty(tcName)) continue;
                    tcName = tcName.trim();
                    String fullName = engine().testTag(tcName, this);
                    if (null == fullName) {
                        logger.warn("Unable to load included template class from name: %s", tcName);
                        continue;
                    }
                    TemplateClass tc = engine().getTemplateClassFromTagName(fullName);
                    if (null == tc) {
                        logger.warn("Unable to load included template class from name: %s", tcName);
                        continue;
                    }
                    includedTemplateClasses.add(tc);
                }
            }
            for (TemplateClass tc: includedTemplateClasses) {
                if (tc.refresh(forceRefresh)) {
                    includedTemplateChanged = true;
                    break;
                }
            }

            if (extendedTemplateChanged && engine().reloadByRestart()) {
                javaByteCode = null;
                enhancedByteCode = null;
                templateInstance = null;
                if (e.reloadByIncClassVersion()) javaClass = null;
                compiled = false;
                engine().classCache.deleteCache(this);
                engine().restart(new ClassReloadException("extended class changed"));
                refresh(forceRefresh);
                return true; // pass refresh state to sub template
            }
            // templateResource.refresh() must be put at first so we make sure resource get refreshed
            boolean refresh = templateResource.refresh() || forceRefresh || (null == javaSource) || includedTemplateChanged || extendedTemplateChanged;
            if (!refresh) return false;

            // now start generate source and compile source to byte code
            buildSourceCode();
            engine().classCache.cacheTemplateClassSource(this); // cache source code for debugging purpose
            if (!codeBuilder.isRythmTemplate()) {
                isValid = false;
                engine().classes.remove(this);
                return false;
            }
            isValid = true;
            //if (!engine().isProdMode()) logger.info(javaSource);
            javaByteCode = null;
            enhancedByteCode = null;
            templateInstance = null;
            if (e.reloadByIncClassVersion()) javaClass = null;
            compiled = false;
            return true;
        } finally {
            refreshing = false;
        }
    }

    public static final String CN_SUFFIX = "__R_T_C__";

    /**
     * Is this class already compiled but not defined ?
     * @return if the class is compiled but not defined
     */
    public boolean isDefinable() {
        return compiled && javaClass != null;
    }

    /**
     * Remove all java source/ byte code and cache
     */
    public void reset() {
        javaByteCode = null;
        enhancedByteCode = null;
        javaSource = null;
        engine().classCache.deleteCache(this);
    }

    /**
     * Compile the class from Java source
     * @return the bytes that comprise the class file
     */
    public byte[] compile() {
        if (null != javaByteCode) return javaByteCode;
        if (null == javaSource) throw new IllegalStateException("Cannot find java source when compiling");
        compiling = true;
        long start = System.currentTimeMillis();
        try {
            engine().classes.compiler.compile(new String[]{name()});
            if (logger.isTraceEnabled()) {
                logger.trace("%sms to compile template: %s", System.currentTimeMillis() - start, getKey());
            }
        } catch (CompileException.CompilerException e) {
            String cn = e.className;
            TemplateClass tc = S.isEqual(cn, name()) ? this : engine().classes.getByClassName(cn);
            if (null == tc) tc = this;
            CompileException ce = new CompileException(tc, e.javaLineNumber, e.message); // init ce before reset java source to get template line info
            if (engine().isProdMode()) {
                logger.error("error compiling java source:\n%s", javaSource);
            }
            javaSource = null; // force parser to regenerate source. This helps to reload after fixing the tag file compilation failure
            throw ce;
        } catch (NullPointerException e) {
            String clazzName = name();
            TemplateClass tc = engine().classes.getByClassName(clazzName);
            if (this != tc) {
                logger.error("tc is not this");
            }
            if (!this.equals(tc)) {
                logger.error("tc not match this");
            }
            logger.error("NPE encountered when compiling template class:" + name());
            throw e;
        } finally {
            compiling = false;
        }

        if (logger.isTraceEnabled()) {
            logger.trace("%sms to compile template class %s", System.currentTimeMillis() - start, getKey());
        }

        return javaByteCode;
    }

    private boolean enhancing = false;
    public byte[] enhance() {
        if (enhancing) throw new IllegalStateException("reenter enhance() call");
        enhancing = true;
        try {
            byte[] bytes = enhancedByteCode;
            if (null == bytes) { bytes = compile(); }
            long start = System.currentTimeMillis();
            for (ITemplateClassEnhancer en: engine().templateClassEnhancers) {
                try {
                    bytes = en.enhance(name(), bytes);
                } catch (Exception e) {
                    logger.warn(e, "Error enhancing template class: %s", getKey());
                }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("%sms to enhance template class %s", System.currentTimeMillis() - start, getKey());
            }
            enhancedByteCode = bytes;
            return bytes;
        } finally {
            enhancing = false;
        }
    }

    /**
     * Unload the class
     */
    public void uncompile() {
        javaClass = null;
    }

    public boolean isClass() {
        return !name().endsWith("package-info");
    }

    public String getPackage() {
        int dot = name().lastIndexOf('.');
        return dot > -1 ? name().substring(0, dot) : "";
    }

    public void compiled(byte[] code, boolean noCache) {
        javaByteCode = code;
        enhancedByteCode = code;
        compiled = true;
        if (!noCache) engine().classCache.cacheTemplateClass(this);
}

    /**
     * Call back when a class is compiled.
     * @param code The bytecode.
     */
    public void compiled(byte[] code) {
        compiled(code, false);
    }

    @Override
    public String toString() {
        return "(compiled:" + compiled + ") " + name();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof TemplateClass) {
            TemplateClass that = (TemplateClass)o;
            return that.getKey().equals(getKey());
        }
        return false;
    }
}
