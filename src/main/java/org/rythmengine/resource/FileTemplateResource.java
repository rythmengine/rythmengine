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

import org.rythmengine.utils.IO;

import java.io.File;

/**
 * Represent a file template resource
 */
public class FileTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private File file;
    private String key;

    @Override
    protected long defCheckInterval() {
        return 1000 * 5;
    }

    public FileTemplateResource(String path, FileResourceLoader loader) {
        super(loader);
        File f = new File(path);
        if (!isValid(f)) {
            File home = loader.getRoot();
            f = new File(home, path);
        }
        file = f;
        key = path.replace('\\', '/');
    }

    FileTemplateResource(File file, FileResourceLoader loader) {
        super(loader);
        this.file = file;
        this.key = file.getPath().replace('\\', '/');
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
        return IO.readContentAsString(file);
    }

    @Override
    public String getSuggestedClassName() {
        return path2CN(key);
    }

}
