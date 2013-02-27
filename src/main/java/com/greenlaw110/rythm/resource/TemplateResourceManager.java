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
package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
        _resourceLoader = engine.conf().get(RythmConfigurationKey.RESOURCE_LOADER_IMPL);
    }

    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) cache.put(resource.getKey(), resource);
        return resource;
    }

    public TemplateClass tryLoadTag(String tagName, TemplateClass tc) {
        if (null != _resourceLoader) return _resourceLoader.tryLoadTag(tagName, tc);
        else return FileTemplateResource.tryLoadTag(tagName, engine, tc);
    }

    public String getFullTagName(TemplateClass tc) {
        if (null != _resourceLoader) return _resourceLoader.getFullTagName(tc);
        else return FileTemplateResource.getFullTagName(tc, engine);
    }

    public ITemplateResource get(File file) {
        return cache(new FileTemplateResource(file, engine));
    }

    public ITemplateResource get(String str) {
        ITemplateResource resource = getFileResource(str);
        if (!resource.isValid()) resource = new StringTemplateResource(str, engine);
        return cache(resource);
    }

    public ITemplateResource getFileResource(String str) {
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
}
