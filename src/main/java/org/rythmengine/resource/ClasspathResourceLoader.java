package org.rythmengine.resource;

/**
 * A simple classpath loader
 */
public class ClasspathResourceLoader extends ResourceLoaderBase {

    @Override
    public String getResourceLoaderRoot() {
        return "/";
    }

    @Override
    public ITemplateResource load(String path) {
        ClasspathTemplateResource ctr = new ClasspathTemplateResource(path, this);
        return ctr;
    }
}
