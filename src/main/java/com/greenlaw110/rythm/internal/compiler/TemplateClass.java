package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.CodeGenerator;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.spi.ITemplateClassEnhancer;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;

import java.io.File;
import java.util.UUID;

/**
 * Define the data structure hold template class/template src/generated java src
 */
public class TemplateClass {
    
    private static final ILogger logger = Logger.get(TemplateClass.class);

    private TemplateClass() {}
    private boolean inner = false;
    public static TemplateClass createInnerClass(String className, byte[] byteCode) {
        TemplateClass tc = new TemplateClass();
        tc.name = className;
        tc.javaByteCode = byteCode;
        tc.inner = true;
        return tc;
    }
    public boolean isInner() {return inner;}
    
    private RythmEngine engine;
    private RythmEngine engine() {
        return null == engine ? Rythm.engine : engine;
    }

    /**
     * The fully qualified class name
     */
    public String name;
    /**
     * the template resource
     */
    public ITemplateResource templateResource;
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
        this(engine);
        templateResource = engine.resourceManager.get(file);
        refresh();
    }

    /**
     * Construct a TemplateClass instance using template source content or file path
     * @param template
     */
    public TemplateClass(String template, RythmEngine engine) {
        this(engine);
        templateResource = engine().resourceManager.get(template);
        refresh();
    }

    /**
     * Return string representation of the template
     * @return
     */
    public String getKey() {
        return templateResource.getKey();
    }

    public ITemplate asTemplate() {
        if (null == templateInstance) {
            try {
                Class<?> clz = engine().classLoader.loadClass(name, true);
                templateInstance = (TemplateBase) clz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return templateInstance.cloneMe();
    }
    
    /**
     * Need to refresh this class !
     */
    public boolean refresh() {
        if (null == name) {
            name = templateResource.getSuggestedClassName();
            engine().classes.add(this);
        }

        boolean modified = templateResource.refresh();
        if (!modified && compiled) return false;

        long start = System.currentTimeMillis();
        javaSource = codeGen.generate(templateResource.asTemplateContent(), name);
        //System.err.println(javaSource);
        if (logger.isTraceEnabled()) {
            logger.trace("%s ms to generate java source for template: %s", System.currentTimeMillis() - start, getKey());
        }
        javaByteCode = null;
        enhancedByteCode = null;
        
        compiled = false;
        return modified;
    }

    private CodeGenerator codeGen = new CodeGenerator();

    public static final String CN_PREFIX = "__R";

    public static String uniqueClassName() {
        return CN_PREFIX + UUID.randomUUID().toString().replace('-', '_');
    }

    /**
     * Is this class already compiled but not defined ?
     * @return if the class is compiled but not defined
     */
    public boolean isDefinable() {
        return compiled && javaClass != null;
    }

    /**
     * Compile the class from Java source
     * @return the bytes that comprise the class file
     */
    public byte[] compile() {
        if (null != javaByteCode) return javaByteCode;
        
        long start = System.currentTimeMillis();
        try {
            engine().classes.compiler.compile(new String[]{name});
        } catch (RuntimeException e) {
            //logger.info(javaSource);
            throw e;
        }

        if (logger.isTraceEnabled()) logger.trace("%sms to compile template class %s", System.currentTimeMillis() - start, getKey());

        return javaByteCode;
    }

    public byte[] enhance() {
        byte[] bytes = enhancedByteCode;
        if (null == bytes) {
            bytes = compile();
            long start = System.currentTimeMillis();
            for (ITemplateClassEnhancer en: engine().templateClassEnhancers) {
                bytes = en.enhance(bytes);
            }
            if (logger.isTraceEnabled()) logger.trace("%sms to enhance template class %s", System.currentTimeMillis() - start, getKey());
            enhancedByteCode = bytes;
        }
        return bytes;
    }

    /**
     * Unload the class
     */
    public void uncompile() {
        javaClass = null;
    }

    public boolean isClass() {
        return !name.endsWith("package-info");
    }

    public String getPackage() {
        int dot = name.lastIndexOf('.');
        return dot > -1 ? name.substring(0, dot) : "";
    }

    /**
     * Call back when a class is compiled.
     * @param code The bytecode.
     */
    public void compiled(byte[] code) {
        javaByteCode = code;
        enhancedByteCode = code;
        compiled = true;
    }

    @Override
    public String toString() {
        return "(compiled:" + compiled + ") " + name;
    }
}
