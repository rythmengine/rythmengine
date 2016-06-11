/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

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
