/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class StringTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private static final long serialVersionUID = -4843989553317549158L;
    private String content;
    private String key;

    public StringTemplateResource(String templateContent) {
        this(templateContent, templateContent);
    }

    public StringTemplateResource(String key, String templateContent) {
        this.key = key;
        this.content = templateContent;
    }

    @Override
    public String getKey() {
        return key;
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
    public String getSuggestedClassName() {
        try {
            return "C" + UUID.nameUUIDFromBytes(getKey().getBytes("utf-8")).toString().replace('-', '_');
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
