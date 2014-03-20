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
package org.rythmengine.resource;

import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.RythmThreadFactory;
import org.rythmengine.internal.compiler.ParamTypeInferencer;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.S;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * The template resource manager manages all template resource loaders and also cache the resource after they
 * get loaded
 */
public class TemplateResourceManager {

    private static final ILogger logger = Logger.get(TemplateResourceManager.class); 
    
    public static final ITemplateResource NULL = new ITemplateResource() {
        @Override
        public Object getKey() {
            return null;
        }

        @Override
        public String getSuggestedClassName() {
            return null;
        }

        @Override
        public String asTemplateContent() {
            return null;
        }

        @Override
        public boolean refresh() {
            return false;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public ICodeType codeType(RythmEngine engine) {
            return null;
        }

        @Override
        public ITemplateResourceLoader getLoader() {
            return null;
        }
    };

    private RythmEngine engine;

    private Map<Object, ITemplateResource> cache = new HashMap<Object, ITemplateResource>();

    private List<ITemplateResourceLoader> loaders;
    
    private FileResourceLoader adhocFileLoader = null;

    // the <key, loader> map allows 
    private Map<Object, ITemplateResourceLoader> whichLoader = new HashMap<Object, ITemplateResourceLoader>();
    
    private boolean typeInference;

    /**
     * Store the String that is NOT a resource
     */
    private static Set<String> blackList = new HashSet<String>();
    
    private static ThreadLocal<Stack<Set<String>>> tmpBlackList = new ThreadLocal<Stack<Set<String>>>() {
        @Override
        protected Stack<Set<String>> initialValue() {
            return new Stack<Set<String>>();
        }
    };
    
    public static void setUpTmpBlackList() {
        tmpBlackList.get().push(new HashSet<String>());
    } 
    
    public static void reportNonResource(String str) {
        tmpBlackList.get().peek().add(str);
    }
    
    public static void commitTmpBlackList() {
        Stack<Set<String>> sss = tmpBlackList.get();
        if (!sss.isEmpty()) {
            Set<String> ss = sss.pop();
            blackList.addAll(ss);
        }
        if (sss.isEmpty()) {
            tmpBlackList.remove();
        }
    }
    
    public static void rollbackTmpBlackList() {
        Stack<Set<String>> sss = tmpBlackList.get();
        if (!sss.isEmpty()) {
            sss.pop();
        }
        if (sss.isEmpty()) {
            tmpBlackList.remove();
        }
    }

    public static void cleanUpTmplBlackList() {
        Stack<Set<String>> ss = tmpBlackList.get();
        if (null != ss) {
            ss.clear();
        }
        tmpBlackList.remove();
    }

    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
        RythmConfiguration conf = engine.conf();
        typeInference = conf.typeInferenceEnabled();
        loaders = new ArrayList(conf.getList(RythmConfigurationKey.RESOURCE_LOADER_IMPLS, ITemplateResourceLoader.class));
        if (!loaders.isEmpty()) {
            Boolean defLoader = conf.get(RythmConfigurationKey.RESOURCE_DEF_LOADER_ENABLED);
            if (!defLoader) {
                return;
            }
        }
        List<File> roots = conf.templateHome();
        for (File root : roots) {
            FileResourceLoader frl = new FileResourceLoader(engine, root);
            if (null == adhocFileLoader) {
                adhocFileLoader = frl;
            }
            loaders.add(frl);
        }
    }

    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) {
            cache.put(resource.getKey(), resource);
        }
        return resource;
    }

    public TemplateClass tryLoadTemplate(String tmplName, TemplateClass callerClass) {
        if (blackList.contains(tmplName)) {
            //logger.info(">>> %s is in the black list", tmplName);
            return null;
        }
        TemplateClass tc = null;
        RythmEngine engine = this.engine;
        for (ITemplateResourceLoader loader : loaders) {
            tc = loader.tryLoadTemplate(tmplName, engine, callerClass);
            if (null != tc) {
                break;
            }
        }
        return tc;
    }
    
    public ITemplateResource get(File file) {
        return cache(new FileTemplateResource(file, adhocFileLoader));
    }

    public ITemplateResource get(String str) {
        ITemplateResource resource = getResource(str);
        if (!resource.isValid()) {
            resource = new StringTemplateResource(str);
        }
        return cache(resource);
    }

    public ITemplateResourceLoader whichLoader(ITemplateResource resource) {
        return whichLoader.get(resource.getKey());
    }

    public ITemplateResource getResource(String str) {
        ITemplateResource resource = cache.get(str);
        if (null != resource) return resource;

        for (ITemplateResourceLoader loader : loaders) {
            resource = loader.load(str);
            if (null != resource && resource.isValid()) {
                whichLoader.put(resource.getKey(), loader);
                break;
            }
        }

        return null == resource ? NULL : cache(resource);
    }
    
    public void scan() {
        for (ITemplateResourceLoader loader : loaders) {
            loader.scan(this);
        }
    }

    public void resourceLoaded(final ITemplateResource resource) {
        resourceLoaded(resource, true);
    }
    
    public TemplateClass resourceLoaded(final ITemplateResource resource, boolean async) {
        final ITemplateResourceLoader loader = resource.getLoader();
        //if (!async) { no async load at the moment
        if (true) {
            whichLoader.put(resource.getKey(), loader);
            return _resourceLoaded(resource);
        } else {
            loadingService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    whichLoader.put(resource.getKey(), loader);
                    _resourceLoaded(resource);
                    return null;
                }
            });
            return null;
        }
    }
    
    private TemplateClass _resourceLoaded(ITemplateResource resource) {
        if (!resource.isValid()) return null;
        String key = S.str(resource.getKey());
        if (typeInference) {
            key += ParamTypeInferencer.uuid();
        }
        RythmEngine engine = this.engine;
        TemplateClass tc = engine.classes().getByTemplate(key);
        if (null == tc) {
            tc = new TemplateClass(resource, engine);
        }
        tc.asTemplate(engine);
        return tc;
    }

    private static class ScannerThreadFactory extends RythmThreadFactory {
        private ScannerThreadFactory() {
            super("rythm-scanner");
        }
    }

    /* 
     * At the moment we don't support parsing templates in parallel, so ...
     */
    private ScheduledExecutorService loadingService = new ScheduledThreadPoolExecutor(1, new ScannerThreadFactory());
    
    public void shutdown() {
        loadingService.shutdown();
    }
}
