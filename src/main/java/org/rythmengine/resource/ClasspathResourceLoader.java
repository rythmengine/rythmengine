package org.rythmengine.resource;

import org.rythmengine.RythmEngine;

/**
 * A simple classpath loader
 */
public class ClasspathResourceLoader extends ResourceLoaderBase {

    private String root;

    public ClasspathResourceLoader(RythmEngine engine, String root) {
        setEngine(engine);
        while (root.startsWith("/") || root.startsWith("\\")) root = root.substring(1);
        if (!root.endsWith("/")) root = root + "/";
        this.root = root;
    }

    @Override
    public String getResourceLoaderRoot() {
        return root;
    }

    @Override
    public ITemplateResource load(String path) {
        while (path.startsWith("/") || path.startsWith("\\")) path = path.substring(1);
        ClasspathTemplateResource ctr = new ClasspathTemplateResource(root + path, this);
        return ctr;
    }
}
