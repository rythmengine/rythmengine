package com.greenlaw110.rythm.internal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greenlaw110.rythm.RythmEngine;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/01/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateClassCache {
    
    //private static final ILogger logger = Logger.get(TemplateClassCache.class);

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
        TemplateClass tc = clsNameIdx.get(name);
        checkUpdate(tc);
        return tc;
    }
    
    public TemplateClass getByTemplate(String name) {
        TemplateClass tc = tmplIdx.get(name);
        checkUpdate(tc);
        return tc;
    }
    
    private void checkUpdate(TemplateClass tc) {
        if (null == tc) return;
        if (null != tc && engine.refreshOnRender()) {
            engine.classLoader.detectChange(tc);
        }
    }
    
    List<TemplateClass> getEmbeddedClasses(String name) {
        List<TemplateClass> l = new ArrayList<TemplateClass>();
        for (String cn: clsNameIdx.keySet()) {
            if (cn.startsWith(name + "$")) {
                l.add(clsNameIdx.get(cn));
            }
        }
        return l;
    }

    public void add(TemplateClass templateClass) {
        clsNameIdx.put(templateClass.name0(), templateClass);
        clsNameIdx.put(templateClass.name(), templateClass);
        if (!templateClass.isInner()) {
            tmplIdx.put(templateClass.templateResource.getKey(), templateClass);
        }
    }

    public void remove(TemplateClass templateClass) {
        if (null == templateClass) return;
        if (templateClass.isInner()) {
            clsNameIdx.remove(templateClass.name());
            return;
        }
        // remove versioned link
        clsNameIdx.remove(templateClass.name());
        // remove unversioned link
        String name0 = templateClass.name0();
        clsNameIdx.remove(name0);
        List<String> embedded = new ArrayList<String>();
        for (String cn: clsNameIdx.keySet()) {
            if (cn.matches(name0 + "v[0-9]+\\$.*")) embedded.add(cn);
        }
        for (String cn: embedded) clsNameIdx.remove(cn);
        if (null != templateClass && null != templateClass.templateResource) tmplIdx.remove(templateClass.getKey());
    }

    public void remove(String name) {
        TemplateClass templateClass = clsNameIdx.get(name);
        remove(templateClass);
    }

    public boolean hasClass(String name) {
        return clsNameIdx.containsKey(name);
    }

    @Override
    public String toString() {
        return clsNameIdx.toString();
    }
}
