package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.util.IO;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
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
        File home = engine().templateHome;
        File f = null;
        if (null != home) {
            f = new File(home, path);
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
        return engine().configuration.getAsLong("rythm.resource.file.interval", null);
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
        return path2CN(file.getPath());
    }
}
