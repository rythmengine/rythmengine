/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

import org.rythmengine.utils.IO;

import java.io.File;

/**
 * Represent a file template resource
 */
public class FileTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private static final long serialVersionUID = -3059476990432671389L;
    private File file;
    private String key;

    @Override
    protected long defCheckInterval() {
        return 1000 * 5;
    }

    public FileTemplateResource(String path, FileResourceLoader loader) {
        super(loader);
        File f = new File(path);
        File home = loader.getRoot();
        String homePath = home.getPath();
        if (!isValid(f)) {
            f = new File(home, path);
        } else if (path.startsWith(homePath)) {
            path = path.substring(homePath.length());
        }
        file = f;
        key = path.replace('\\', '/');
    }

    FileTemplateResource(File file, FileResourceLoader loader) {
        super(loader);
        this.file = file;
        String path = file.getPath();
        String homePath = loader.getRoot().getPath();
        if (path.startsWith(homePath)) {
            path = path.substring(homePath.length());
        }
        this.key = path.replace('\\', '/');
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
