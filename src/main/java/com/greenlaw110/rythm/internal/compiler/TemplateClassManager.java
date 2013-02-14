/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.internal.compiler;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/01/12
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateClassManager {
    protected final ILogger logger = Logger.get(TemplateClassManager.class);

    //private static final ILogger logger = Logger.get(TemplateClassCache.class);

    public RythmEngine engine = null;

    /**
     * Reference to the eclipse compiler.
     */
    TemplateCompiler compiler = new TemplateCompiler(this);
    /**
     * Index template class with class name
     */
    public Map<String, TemplateClass> clsNameIdx = new HashMap<String, TemplateClass>();
    /**
     * Index template class with inline template content or template file name
     */
    public Map<Object, TemplateClass> tmplIdx = new HashMap<Object, TemplateClass>();

    public TemplateClassManager(RythmEngine engine) {
        if (null == engine) throw new NullPointerException();
        this.engine = engine;
    }

    /**
     * Clear the classCache cache
     */
    public void clear() {
        clsNameIdx = new HashMap<String, TemplateClass>();
        tmplIdx = new HashMap<Object, TemplateClass>();
    }

    /**
     * All loaded classes.
     *
     * @return All loaded classes
     */
    public List<TemplateClass> all() {
        return new ArrayList<TemplateClass>(clsNameIdx.values());
    }

    /**
     * Get a class by name
     *
     * @param name The fully qualified class name
     * @return The TemplateClass or null
     */
    public TemplateClass getByClassName(String name) {
        TemplateClass tc = clsNameIdx.get(name);
        checkUpdate(tc);
        return tc;
    }

    public TemplateClass getByTemplate(Object name) {
        TemplateClass tc = tmplIdx.get(name);
        if (null == tc) {
            // try to see if resourceLoader has some kind of name transform
            ITemplateResource r = engine.resourceManager().getFileResource(name.toString());
            if (null == r) return null;
            tc = tmplIdx.get(r.getKey());
        }
        checkUpdate(tc);
        return tc;
    }

    private void checkUpdate(TemplateClass tc) {
        if (null == tc) return;
        if (null != tc && engine.isDevMode()) {
            if (logger.isTraceEnabled()) {
                logger.trace("checkUpdate for template: %s", tc.getKey());
            }
            try {
                engine.classLoader().detectChange(tc);
            } catch (ClassReloadException e) {
                engine.restart(e);
            }
        }
    }

    List<TemplateClass> getEmbeddedClasses(String name) {
        List<TemplateClass> l = new ArrayList<TemplateClass>();
        for (String cn : clsNameIdx.keySet()) {
            if (cn.startsWith(name + "$")) {
                l.add(clsNameIdx.get(cn));
            }
        }
        return l;
    }
    
    public void add(Object key, TemplateClass templateClass) {
        tmplIdx.put(key, templateClass);
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
        for (String cn : clsNameIdx.keySet()) {
            if (cn.matches(name0 + "v[0-9]+\\$.*")) embedded.add(cn);
        }
        for (String cn : embedded) clsNameIdx.remove(cn);
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
