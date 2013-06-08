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

    public FileTemplateResource(String path) {
        this(path, null);
    }

    public FileTemplateResource(String path, RythmEngine engine) {
        super(engine);
        File f = new File(path);
        if (null == f || !f.canRead()) {
            File home = engine().conf().templateHome();
            if (null != home) {
                f = new File(home, path);
            }
        }
        file = f;
        key = path.replace("\\", "/");
    }

    public FileTemplateResource(File templateFile) {
        this(templateFile, null);
    }

    public FileTemplateResource(File templateFile, RythmEngine engine) {
        super(engine);
        file = templateFile;
        key = file.getPath();
        File home = engine().conf().templateHome();
        String homePath = null == home ? null : home.getPath();
        if (null != homePath && key.startsWith(homePath)) {
            key = key.replace(homePath, "");
            if (key.startsWith("/") || key.startsWith("\\")) {
                key = key.substring(1);
            }
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public boolean isValid() {
        return null != file && !file.isDirectory() && file.canRead();
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
