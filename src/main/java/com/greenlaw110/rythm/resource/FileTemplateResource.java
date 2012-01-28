package com.greenlaw110.rythm.resource;

import java.io.File;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.util.IO;

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
        File home = engine().templateHome;
        File tagHome = engine().tagHome;
        File f = null;
        if (null != home) {
            f = new File(home, path);
        }
        if (null == f || !f.canRead()) {
            // try tag home
            if (null != tagHome) f = new File(tagHome, path);
        }
        if (null == f || !f.canRead()) {
            f = new File(path);
        }
        file = f;
        key = path;
        
        if (null != tagHome && isValid()) {
            // set tag name if this file is found under tag home
            String tagPath = tagHome.getAbsolutePath();
            String filePath = f.getAbsolutePath();
            if (filePath.startsWith(tagPath)) {
                this.tagName = retrieveTagName(tagHome, f);
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
    
    public static void main(String[] args) {
        File tagHome = new File("W:\\_lgl\\greenscript-1.2\\java\\play\\app\\views\\tags\\");
        File tagFile = new File("W:\\_lgl\\greenscript-1.2\\java\\play\\app\\views\\tags\\greenscript\\css.html");
        System.out.println(retrieveTagName(tagHome, tagFile));
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

    @Override
    public String tagName() {
        return tagName;
    }

    public static void tryLoadTag(String tagName, RythmEngine engine) {
        if (null == engine) engine = Rythm.engine;
        if (engine.tags.containsKey(tagName)) return;
        tagName = tagName.replace('.', '/');
        final String[] suffixes = {
                ".html",
                ".json",
                ".tag"
        };
        File tagFile = null;
        for (String suffix: suffixes) {
            String name = tagName + suffix;
            tagFile = new File(engine.tagHome, name);
            if (tagFile.canRead()) {
                try {
                    FileTemplateResource tr = new FileTemplateResource(tagFile);
                    TemplateClass tc = engine.classes.getByTemplate(tr.getKey());
                    if (null == tc) {
                        tc = new TemplateClass(tr, engine);
                        ITag tag = (ITag)tc.asTemplate();
                        if (null != tag) engine.registerTag(tag);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
