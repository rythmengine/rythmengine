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

import java.io.File;

/**
 * Implement a File resource loader
 */
public class FileResourceLoader extends ResourceLoaderBase {

    private File root;

    public FileResourceLoader(RythmEngine engine, File root) {
        this.root = root;
        setEngine(engine);
    }

    @Override
    public ITemplateResource load(String path) {
        return new FileTemplateResource(path, this);
    }

    @Override
    public String getResourceLoaderRoot() {
        return root.getPath();
    }
    
    public File getRoot() {
        return root;
    }

    @Override
    public void scan(TemplateResourceManager manager) {
        File file = new File(getResourceLoaderRoot());
        scan_(file, manager);
    }

    private void scan_(File file, TemplateResourceManager manager) {
        if (file.isFile() && file.canRead()) {
            load_(file, manager);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null == files) return;
            for (File f: files) {
                scan_(f, manager);
            }
        }
    }
    
    private void load_(final File file, final TemplateResourceManager manager) {
        manager.resourceLoaded(new FileTemplateResource(file, this));
    }
}
