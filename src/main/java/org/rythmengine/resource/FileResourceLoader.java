package org.rythmengine.resource;

import org.rythmengine.RythmEngine;

import java.io.File;

/**
 * Implement a File resource loader
 */
public class FileResourceLoader extends ResourceLoaderBase {

    private RythmEngine engine;
    private String root;

    public FileResourceLoader(RythmEngine engine) {
        this.engine = engine;
        this.root = engine.conf().templateHome().getPath().replace('\\', '/');
    }

    @Override
    public ITemplateResource load(String path) {
        return new FileTemplateResource(path);
    }

    @Override
    public String getResourceLoaderRoot() {
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
        } else {
            File[] files = file.listFiles();
            if (null == files) return;
            for (File f: files) {
                scan_(f, manager);
            }
        }
    }
    
    private void load_(final File file, final TemplateResourceManager manager) {
        manager.resourceLoaded(new FileTemplateResource(file, engine), this);
    }
}
