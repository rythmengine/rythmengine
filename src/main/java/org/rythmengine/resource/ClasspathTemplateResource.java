/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

import org.rythmengine.utils.IO;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClasspathTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private static final long serialVersionUID = -164305020378609839L;
    private URL url;
    private String key;
    private String key2;

    ClasspathTemplateResource(String path, ClasspathResourceLoader loader) {
        super(loader);
        ClassLoader cl = loader.getEngine().classLoader();

        // strip leading slash so path will work with classes in a JAR file
        while (path.startsWith("/")) path = path.substring(1);

        url = cl.getResource(path);

        if( !isValid() ) {
            url = cl.getResource( loader.getResourceLoaderRoot() + "/" + path );
        }

        key = path;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey2(String key) {
        key2 = key;
    }

    public String getKey2() {
        return key2;
    }

    @Override
    public String reload() {
        return IO.readContentAsString(url);
    }

    @Override
    protected long lastModified() {
        if (getEngine().isProdMode()) return 0;

        String fileName;
        if ("file".equals(url.getProtocol())) {
            fileName = url.getFile();
        } else if ("jar".equals(url.getProtocol())) {
            try {
                java.net.JarURLConnection jarUrl = (java.net.JarURLConnection) url.openConnection();
                fileName = jarUrl.getJarFile().getName();
            } catch (Exception e) {
                return System.currentTimeMillis() + 1;
            }
        } else {
            return System.currentTimeMillis() + 1;
        }

        java.io.File file = new java.io.File(fileName);
        return file.lastModified();
    }

    @Override
    public boolean isValid() {
        return null != url;
    }

    @Override
    protected long defCheckInterval() {
        return -1;
    }

    @Override
    protected Long userCheckInterval() {
        return Long.valueOf(1000 * 5);
    }

    @Override
    public String getSuggestedClassName() {
        return path2CN(key);
    }

}
