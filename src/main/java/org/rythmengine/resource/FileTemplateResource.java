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

import org.rythmengine.utils.IO;

import java.io.File;

/**
 * Represent a file template resource
 */
public class FileTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private static final long serialVersionUID = -3059476990432671389L;
    private File file;
    private String key;

    @Override
    protected long defCheckInterval() {
        return 1000 * 5;
    }

    /**
     * construct me from the givne path and loader
     * @param path
     * @param loader
     */
    public FileTemplateResource(String path, FileResourceLoader loader) {
        super(loader);
        File f = new File(path);
        File home = loader.getRoot();
        String homePath = home.getPath();
        if (!isValid(f)) {
            f = new File(home, path);
        } else if (path.startsWith(homePath)) {
            path = path.substring(homePath.length());
        }
        file = f;
        key = path.replace('\\', '/');
    }

    /**
     * construct me from the given file and loader
     * @param file
     * @param loader
     */
    FileTemplateResource(File file, FileResourceLoader loader) {
        super(loader);
        this.file = file;
        String path = file.getPath();
        String homePath = loader.getRoot().getPath();
        if (path.startsWith(homePath)) {
            path = path.substring(homePath.length());
        }
        this.key = path.replace('\\', '/');
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    protected boolean isValid(File file) {
        return null != file && !file.isDirectory() && file.canRead();
    }

    @Override
    public boolean isValid() {
        return isValid(file);
    }

    @Override
    protected String reload() {
        String text=IO.readContentAsString(file);
        return text;
    }

    @Override
    public String getSuggestedClassName() {
        return path2CN(key);
    }

}
