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
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.RythmThreadFactory;
import org.rythmengine.internal.compiler.ParamTypeInferencer;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.utils.S;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateResourceManager {

    private RythmEngine engine;

    private Map<Object, ITemplateResource> cache = new HashMap<Object, ITemplateResource>();

    private ITemplateResourceLoader _resourceLoader = null;
    
    private boolean typeInference;

    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
        _resourceLoader = engine.conf().get(RythmConfigurationKey.RESOURCE_LOADER_IMPL);
        typeInference = engine.conf().typeInferenceEnabled();
    }

    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) cache.put(resource.getKey(), resource);
        return resource;
    }

    public TemplateClass tryLoadTemplate(String tagName, TemplateClass tc) {
        if (null != _resourceLoader) return _resourceLoader.tryLoadTemplate(tagName, engine, tc);
        else return FileTemplateResource.tryLoadTemplate(tagName, engine, tc);
    }

    public String getFullTagName(TemplateClass tc) {
        if (null != _resourceLoader) return _resourceLoader.getFullName(tc, engine);
        else return FileTemplateResource.getFullTagName(tc, engine);
    }

    public ITemplateResource get(File file) {
        return cache(new FileTemplateResource(file, engine));
    }

    public ITemplateResource get(String str) {
        ITemplateResource resource = getResource(str);
        if (!resource.isValid()) resource = new StringTemplateResource(str, engine);
        return cache(resource);
    }

    public ITemplateResource getResource(String str) {
        ITemplateResource resource = cache.get(str);
        if (null != resource) return resource;

        if (null != _resourceLoader) resource = _resourceLoader.load(str);
        if (null != resource) return resource;

        // try build-in loader
        resource = new FileTemplateResource(str, engine);
        if (!resource.isValid()) {
            resource = new ClasspathTemplateResource(str, engine);
        }
        return cache(resource);
    }
    
    public void resourceLoaded(ITemplateResource resource) {
        if (!resource.isValid()) return;
        String key = S.str(resource.getKey());
        if (typeInference) {
            key += ParamTypeInferencer.uuid();
        }
        RythmEngine engine = this.engine;
        TemplateClass tc = engine.classes().getByTemplate(key);
        if (null == tc) {
            tc = new TemplateClass(resource, engine);
            //engine.classes().add(key, tc);
        }
        tc.asTemplate(engine);
//        if (null == t) return;
//        String fullTagName = engine.resourceManager().getFullTagName(tc);
//        tc.setFullName(fullTagName);
//        engine.registerTemplate(fullTagName, t);
    }

    private static class ScannerThreadFactory extends RythmThreadFactory {
        private ScannerThreadFactory() {
            super("rythm-scanner");
        }
    }

    private ScheduledExecutorService loadingService = new ScheduledThreadPoolExecutor(10, new ScannerThreadFactory());
    
    public void scan(File home) {
        String path = null == home ? null : home.getAbsolutePath();
        if (null != _resourceLoader) _resourceLoader.scan(path, this);
        else {
            scan_(home);
        }
    }
    
    private void scan_(File file) {
        if (file.isFile() && file.canRead()) {
            load_(file);
        } else {
            File[] files = file.listFiles();
            if (null == files) return;
            for (File f: files) {
                scan_(f);
            }
        }
    }
    
    private void load_(final File file) {
        loadingService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ITemplateResource resource = new FileTemplateResource(file, engine);
                resourceLoaded(resource);
                return null;
            }
        });
    }
    
    public void shutdown() {
        loadingService.shutdown();
    }
}
