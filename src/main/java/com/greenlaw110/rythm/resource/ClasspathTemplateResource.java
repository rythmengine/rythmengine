package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.utils.IO;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClasspathTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private URL url;
    private String key;

    public ClasspathTemplateResource(String path) {
        this(path, null);
    }

    public ClasspathTemplateResource(String path, RythmEngine engine) {
        super(engine);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) {
            cl = Rythm.class.getClassLoader();
        }
        // strip leading slash so path will work with classes in a JAR file
        while (path.startsWith("/")) path = path.substring(1);
        url = cl.getResource(path);
        key = path;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String reload() {
        return IO.readContentAsString(url);
    }

    @Override
    protected long lastModified() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return null != url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ClasspathTemplateResource) {
            ClasspathTemplateResource that = (ClasspathTemplateResource)obj;
            return that.getKey().equals(this.getKey());
        }
        return false;
    }

    @Override
    protected long defCheckInterval() {
        return -1;
    }

    @Override
    protected Long userCheckInterval() {
        return engine().configuration.getAsLong("rythm.resource.classpath.interval", Long.valueOf(1000 * 5));
    }

    @Override
    public String getSuggestedClassName() {
        return path2CN(url.getPath());
    }

    public static void main(String[] args) {
        ClasspathTemplateResource cr = new ClasspathTemplateResource("abc23.x");
        System.out.println(cr.isValid());
    }
}
