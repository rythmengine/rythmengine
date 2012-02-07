package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.RythmEngine;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringTemplateResource extends TemplateResourceBase implements ITemplateResource {
    
    private String content;
    
    public StringTemplateResource(String templateContent) {
        content = templateContent;
    }
    
    public StringTemplateResource(String templateContent, RythmEngine engine) {
        super(engine);
        content = templateContent;
    }
    
    @Override
    public String getKey() {
        return content;
    }

    @Override
    public String asTemplateContent() {
        return content;
    }

    @Override
    protected long lastModified() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected long defCheckInterval() {
        return -1;
    }

    @Override
    protected Long userCheckInterval() {
        return null;
    }

    @Override
    protected String reload() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof StringTemplateResource) {
            StringTemplateResource that = (StringTemplateResource)obj;
            return that.getKey().equals(getKey());
        }
        return false;
    }
}
