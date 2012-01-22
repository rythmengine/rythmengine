package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.TemplateResourceManager;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/01/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateClassCache {
    
    private static final ILogger logger = Logger.get(TemplateClassCache.class);

    public RythmEngine engine = null;

    /**
     * Reference to the eclipse compiler.
     */
    TemplateCompiler compiler = new TemplateCompiler(this);
    /**
     * Index template class with class name
     */
    Map<String, TemplateClass> clsNameIdx = new HashMap<String, TemplateClass>();
    /**
     * Index template class with inline template content or template file name
     */
    Map<String, TemplateClass> tmplIdx = new HashMap<String, TemplateClass>();

    public TemplateClassCache(RythmEngine engine) {
        if (null == engine) throw new NullPointerException();
        this.engine = engine;
    }

    private TemplateResourceManager resourceManager() {
        return engine.resourceManager;
    }

    /**
     * Clear the classCache cache
     */
    public void clear() {
        clsNameIdx = new HashMap<String, TemplateClass>();
    }

    /**
     * All loaded classes.
     * @return All loaded classes
     */
    public List<TemplateClass> all() {
        return new ArrayList<TemplateClass>(clsNameIdx.values());
    }

    /**
     * Get a class by name
     * @param name The fully qualified class name
     * @return The TemplateClass or null
     */
    public TemplateClass getByClassName(String name) {
        return clsNameIdx.get(name);
    }
    
    public TemplateClass getByTemplate(String name) {
        return tmplIdx.get(name);
    }

    public void add(TemplateClass templateClass) {
        clsNameIdx.put(templateClass.name, templateClass);
        if (!templateClass.isInner()) {
            tmplIdx.put(templateClass.templateResource.getKey(), templateClass);
        }
    }

    public void remove(TemplateClass templateClass) {
        clsNameIdx.remove(templateClass.name);
    }

    public void remove(String name) {
        clsNameIdx.remove(name);
    }

    public boolean hasClass(String name) {
        return clsNameIdx.containsKey(name);
    }

    @Override
    public String toString() {
        return clsNameIdx.toString();
    }
}
