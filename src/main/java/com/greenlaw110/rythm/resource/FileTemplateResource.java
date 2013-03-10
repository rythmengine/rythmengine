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

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.ITag;
import com.greenlaw110.rythm.utils.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private ILogger logger = Logger.get(FileTemplateResource.class);
    private File file;
    private String key;
    private String tagName;

    @Override
    protected long defCheckInterval() {
        return 1000 * 5;
    }

    public FileTemplateResource(String path) {
        this(path, null);
    }

    public FileTemplateResource(String path, RythmEngine engine) {
        super(engine);
        File home = engine().conf().templateHome();
        File f = null;
        if (null != home) {
            f = new File(home, path);
        } else {
        }
        if (null == f || !f.canRead()) {
            f = new File(path);
        }
        file = f;
        key = path;
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

    private static String retrieveTagName(File tagHome, File tagFile) {
        String tagPath = tagHome.getAbsolutePath();
        String filePath = tagFile.getAbsolutePath();
        String tagName = null;
        if (filePath.startsWith(tagPath)) {
            tagName = filePath.substring(tagPath.length());
            while (tagName.startsWith("/") || tagName.startsWith("\\")) {
                tagName = tagName.substring(1);
            }
            tagName = tagName.replace('\\', '.');
            tagName = tagName.replace('/', '.');
            int dot = tagName.lastIndexOf(".");
            tagName = tagName.substring(0, dot);
        }
        return tagName;
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
    protected Long userCheckInterval() {
        //return engine().configuration.getAsLong("rythm.resource.file.interval", null);
        return null;
    }

    @Override
    public boolean isValid() {
        return null != file && file.canRead();
    }

    @Override
    protected String reload() {
        return IO.readContentAsString(file);
    }

    @Override
    public String getSuggestedClassName() {
        return path2CN(key);
    }

    @Override
    public String tagName() {
        return tagName;
    }

    public static String getFullTagName(TemplateClass tc, RythmEngine engine) {
        if (null == engine) engine = Rythm.engine();
        String key = tc.getKey().toString();
        File home = engine.conf().templateHome();
        if (key.startsWith("/") || key.startsWith("\\")) key = key.substring(1);
        if (null != home && key.startsWith(home.getPath())) {
            key = key.replace(home.getPath(), "");
        }
        if (key.startsWith("/") || key.startsWith("\\")) key = key.substring(1);
        int pos = key.lastIndexOf(".");
        if (-1 != pos) key = key.substring(0, pos);
        return key.replace('/', '.').replace('\\', '.');
    }

    public static TemplateClass tryLoadTag(String tagName, RythmEngine engine, TemplateClass templateClass) {
        if (null == engine) engine = Rythm.engine();
        if (engine.hasTag(tagName)) return null;
        tagName = tagName.replace('.', '/');
        final String[] suffixes = {
                ".rythm",
                ".html",
                ".json",
                ".xml",
                ".csv",
                ".tag",
                ".txt",
                ""
        };
        File tagFile;
        List<String> roots = new ArrayList<String>();
        File home = engine.conf().templateHome();
        String root = home.getPath();

        // call tag with import path
        if (null != templateClass.importPaths) {
            for (String s : templateClass.importPaths) {
                roots.add(root + File.separator + s.replace('.', File.separatorChar));
            }
        }

        final String tagName0 = tagName;
        // call tag using relative path
        String currentPath = templateClass.getKey().toString();
        int pos = currentPath.lastIndexOf("/");
        if (-1 == pos) {
            pos = currentPath.lastIndexOf(File.separator);
        }
        if (-1 != pos) {
            currentPath = currentPath.substring(0, pos);
            if (currentPath.startsWith("/") || currentPath.startsWith(File.separator)) currentPath = currentPath.substring(1);
            if (!currentPath.startsWith(root)) currentPath = root + File.separator + currentPath;
            roots.add(currentPath);
        }

        // add the default root at last
        roots.add(home.getPath());

        for (String r : roots) {
            tagName = r + File.separator + tagName0;
            for (String suffix : suffixes) {
                String name = tagName + suffix;

                tagFile = new File(name);
                ITemplateResource tr = tagFile.canRead() ? new FileTemplateResource(tagFile, engine) : new ClasspathTemplateResource(name, engine);
                if (tr.isValid()) {
                    try {
                        TemplateClass tc = engine.classes().getByTemplate(tr.getKey());
                        if (null == tc) {
                            tc = new TemplateClass(tr, engine);
                        }
                        try {
                            ITag tag = (ITag) tc.asTemplate();
                            if (null != tag) {
                                String fullName = getFullTagName(tc, engine);
                                tc.setFullName(fullName);
                                engine.registerTag(fullName, tag);
                                return tc;
                            }
                        } catch (Exception e) {
                            return tc;
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        return null;
    }
}
