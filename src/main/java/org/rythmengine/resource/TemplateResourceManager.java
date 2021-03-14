/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.rythmengine.RythmEngine;
import org.rythmengine.Sandbox;
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
import java.net.URI;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * The template resource manager manages all template resource loaders and also cache the resource after they
 * get loaded
 */
public class TemplateResourceManager {

    @SuppressWarnings("unused")
    private static final ILogger logger = Logger.get(TemplateResourceManager.class); 
    
    /**
     * NULL TemplateResource
     */
    @SuppressWarnings("serial")
    public static final ITemplateResource NULL = new ITemplateResource() {
        // this NULL TemplateResource can actually carry an error
        // might not be necessary but is there in any case
        private Throwable error;

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

        @Override
        public Throwable getError() {
          return error;
        }

        @Override
        public void setError(Throwable error) {
          this.error=error;
        }
    };

    private RythmEngine engine;

    private Map<Object, ITemplateResource> cache = new ConcurrentHashMap<Object, ITemplateResource>();

    private List<ITemplateResourceLoader> loaders;
    
    private FileResourceLoader adhocFileLoader = null;

    // the <key, loader> map allows 
    private Map<Object, ITemplateResourceLoader> whichLoader = new ConcurrentHashMap<Object, ITemplateResourceLoader>();
    
    private boolean typeInference;

    /**
     * Store the String that is NOT a resource
     */
    private static Set<String> blackList = new CopyOnWriteArraySet<String>();
    
    private static ThreadLocal<Deque<Set<String>>> tmpBlackList = new ThreadLocal<Deque<Set<String>>>() {
        @Override
        protected Deque<Set<String>> initialValue() {
            return new ConcurrentLinkedDeque<Set<String>>();
        }
    };
    
    public static void setUpTmpBlackList() {
        tmpBlackList.get().push(new CopyOnWriteArraySet<String>());
    } 
    
    public static void reportNonResource(String str) {
        Deque<Set<String>> ss = tmpBlackList.get();
        if (ss.isEmpty()) {
            // invoked dynamically when running @invoke(...)
            tmpBlackList.remove();
            blackList.add(str);
        } else {
            ss.peek().add(str);
        }
    }
    
    public static void commitTmpBlackList() {
        Deque<Set<String>> sss = tmpBlackList.get();
        if (!sss.isEmpty()) {
            Set<String> ss = sss.pop();
            blackList.addAll(ss);
        }
        if (sss.isEmpty()) {
            tmpBlackList.remove();
        }
    }
    
    public static void rollbackTmpBlackList() {
        Deque<Set<String>> sss = tmpBlackList.get();
        if (!sss.isEmpty()) {
            sss.pop();
        }
        if (sss.isEmpty()) {
            tmpBlackList.remove();
        }
    }

    public static void cleanUpTmplBlackList() {
//        Stack<Set<String>> ss = tmpBlackList.get();
//        if (null != ss) {
//            ss.clear();
//        }
        tmpBlackList.remove();
    }

    /**
     * construct the TemplateResourceManager for the give engine
     * @param engine
     */
    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
        RythmConfiguration conf = engine.conf();
        typeInference = conf.typeInferenceEnabled();
        loaders = new CopyOnWriteArrayList<>(conf.getList(RythmConfigurationKey.RESOURCE_LOADER_IMPLS, ITemplateResourceLoader.class));
        if (!loaders.isEmpty()) {
            for (ITemplateResourceLoader loader: loaders) {
                loader.setEngine(this.engine);
            }
            Boolean defLoader = conf.get(RythmConfigurationKey.RESOURCE_DEF_LOADER_ENABLED);
            if (!defLoader) {
                return;
            }
        }
        List<URI> roots = conf.templateHome();
        for (URI root : roots) {
            if (null == root) continue;
            String scheme = root.getScheme();
            if (S.eq(scheme, "jar")) {
                String s = root.getSchemeSpecificPart();
                int pos = s.indexOf(".jar!");
                String home = s.substring(pos + 5);
                ClasspathResourceLoader crl = new ClasspathResourceLoader(engine, home);
                loaders.add(crl);
            } else if (S.eq(scheme, "file")) {
                FileResourceLoader frl = new FileResourceLoader(engine, new File(root.getPath()));
                if (null == adhocFileLoader) {
                    adhocFileLoader = frl;
                }
                loaders.add(frl);
            }
        }
    }

    public void addResourceLoader(ITemplateResourceLoader loader) {
        if (!loaders.contains(loader)) loaders.add(loader);
    }

    public void prependResourceLoader(ITemplateResourceLoader loader) {
        if (!loaders.contains(loader)) loaders.add(0, loader);
    }

    private ITemplateResource cache(ITemplateResource resource) {
        return cache(null, resource);
    }

    private ITemplateResource cache(Object key, ITemplateResource resource) {
        if (resource.isValid()) {
            cache.put(resource.getKey(), resource);
            if (null != key) {
                cache.put(key, resource);
            }
        }
        return resource;
    }

    public TemplateClass tryLoadTemplate(String tmplName, TemplateClass callerClass, ICodeType codeType) {
        if (blackList.contains(tmplName)) {
            //logger.info(">>> %s is in the black list", tmplName);
            return null;
        }
        TemplateClass tc = null;
        RythmEngine engine = this.engine;
        if (null != callerClass) {
            ITemplateResourceLoader loader = whichLoader(callerClass.templateResource);
            if (null != loader) {
                return loader.tryLoadTemplate(tmplName, engine, callerClass, codeType);
            }
        }
        for (ITemplateResourceLoader loader : loaders) {
            tc = loader.tryLoadTemplate(tmplName, engine, callerClass, codeType);
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

    public ITemplateResource getResource(String key) {
        ITemplateResource resource = cache.get(key);
        if (null != resource) return resource;

        if (Sandbox.isRestricted()) return NULL;

        for (ITemplateResourceLoader loader : loaders) {
            resource = loader.load(key);
            if (null != resource && resource.isValid()) {
                whichLoader.put(resource.getKey(), loader);
                whichLoader.put(key, loader);
                if (key != resource.getKey() && resource instanceof ClasspathTemplateResource) {
                    ((ClasspathTemplateResource) resource).setKey2(key);
                }
                break;
            }
        }

        return null == resource ? NULL : cache(key, resource);
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
