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
package org.rythmengine.extension;

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.TemplateResourceManager;

/**
 * Defines the behavior of a template resource loader
 */
public interface ITemplateResourceLoader {

    void setEngine(RythmEngine engine);
    
    RythmEngine getEngine();

    /**
     * Return root path of this resource loader
     * 
     * @return the root path
     */
    String getResourceLoaderRoot();

    /**
     * Load template resource by path
     * @param path
     * @return Loaded template resource
     */
    ITemplateResource load(String path);

    /**
     * Try to load a template tag with tag name.
     * 
     * @param tmplName
     * @param engine
     * @param callerTemplateClass
     * @return template class if found, or <tt>null</tt> if not found
     */
    TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerTemplateClass);

    /**
     * Scan the folder and try to load all template files under the folder.
     * Once a resource is located, it should be passed to the 
     * {@link org.rythmengine.resource.TemplateResourceManager resource manager} by 
     * {@link org.rythmengine.resource.TemplateResourceManager#resourceLoaded(ITemplateResource)} call
     * 
     * <p>Note it is up to the loader implementation to decide the root path where
     * to start the scan</p>
     * 
     * @param manager the resource manager
     */
    void scan(TemplateResourceManager manager);

}
