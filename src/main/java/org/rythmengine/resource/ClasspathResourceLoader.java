package org.rythmengine.resource;

/**
 * A simple classpath loader
 */
public class ClasspathResourceLoader extends ResourceLoaderBase {

    private String root;

    public ClasspathResourceLoader() {
         this("");

    }

    public ClasspathResourceLoader(String root) {
        super();
        this.root = root;
    }

    @Override
    public String getResourceLoaderRoot() {
        return root;
    }

    @Override
    public ITemplateResource load(String path) {
        ClasspathTemplateResource ctr = new ClasspathTemplateResource(path, this);
        return ctr;
    }
}
