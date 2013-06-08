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
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.RythmThreadFactory;
import org.rythmengine.internal.compiler.ParamTypeInferencer;
import org.rythmengine.internal.compiler.TemplateClass;
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

    private RythmEngine engine;

    private Map<Object, ITemplateResource> cache = new HashMap<Object, ITemplateResource>();

    private List<ITemplateResourceLoader> loaders;
    private ITemplateResourceLoader fileLoader;
    private Iterable<ITemplateResourceLoader> allLoaders;

    // the <key, loader> map allows 
    private Map<Object, ITemplateResourceLoader> whichLoader = new HashMap<Object, ITemplateResourceLoader>();
    
    private boolean typeInference;

    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
        RythmConfiguration conf = engine.conf();
        loaders = conf.getList(RythmConfigurationKey.RESOURCE_LOADER_IMPLS, ITemplateResourceLoader.class);
        fileLoader = new FileResourceLoader(engine);
        allLoaders = new Iterable<ITemplateResourceLoader>() {
            @Override
            public Iterator<ITemplateResourceLoader> iterator() {
                return new Iterator<ITemplateResourceLoader>() {
                    private int cursor;
                    private int size = loaders.size() + 1;
                    public boolean hasNext() {
                        return cursor < size;
                    }

                    @Override
                    public ITemplateResourceLoader next() {
                        if (cursor < size - 1) {
                            return loaders.get(cursor++);
                        } else {
                            cursor++;
                            return fileLoader;
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        
        typeInference = conf.typeInferenceEnabled();
    }

    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) {
            cache.put(resource.getKey(), resource);
        }
        return resource;
    }

    public TemplateClass tryLoadTemplate(String tmplName, TemplateClass callerClass) {
        TemplateClass tc = null;
        RythmEngine engine = this.engine;
        for (ITemplateResourceLoader loader : allLoaders) {
            tc = loader.tryLoadTemplate(tmplName, engine, callerClass);
            if (null != tc) {
                break;
            }
        }
        return tc;
    }
    
    public ITemplateResource get(File file) {
        return cache(new FileTemplateResource(file, engine));
    }

    public ITemplateResource get(String str) {
        ITemplateResource resource = getResource(str);
        if (!resource.isValid()) resource = new StringTemplateResource(str, engine);
        return cache(resource);
    }

    public ITemplateResourceLoader whichLoader(ITemplateResource resource) {
        return whichLoader.get(resource.getKey());
    }

    public ITemplateResource getResource(String str) {
        ITemplateResource resource = cache.get(str);
        if (null != resource) return resource;

        for (ITemplateResourceLoader loader : allLoaders) {
            resource = loader.load(str);
            if (null != resource && resource.isValid()) {
                whichLoader.put(resource.getKey(), loader);
                break;
            }
        }

        return cache(resource);
    }
    
    public void scan() {
        for (ITemplateResourceLoader loader : allLoaders) {
            loader.scan(this);
        }
    }

    public void resourceLoaded(final ITemplateResource resource, final ITemplateResourceLoader loader) {
        resourceLoaded(resource, loader, true);
    }
    
    public TemplateClass resourceLoaded(final ITemplateResource resource, final ITemplateResourceLoader loader, boolean async) {
        if (!async) {
            whichLoader.put(resource.getKey(), loader);
            return resourceLoaded(resource);
        } else {
            loadingService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    whichLoader.put(resource.getKey(), loader);
                    resourceLoaded(resource);
                    return null;
                }
            });
            return null;
        }
    }
    
    private TemplateClass resourceLoaded(ITemplateResource resource) {
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

    private ScheduledExecutorService loadingService = new ScheduledThreadPoolExecutor(10, new ScannerThreadFactory());
    
    public void shutdown() {
        loadingService.shutdown();
    }
}
