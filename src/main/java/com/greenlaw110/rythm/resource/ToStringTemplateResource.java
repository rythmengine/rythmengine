package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.dialect.AutoToString;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToStringTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private AutoToString.AutoToStringData meta;

    public ToStringTemplateResource(AutoToString.AutoToStringData data) {
        meta = data;
    }

    public ToStringTemplateResource(AutoToString.AutoToStringData data  , RythmEngine engine) {
        super(engine);
        meta = data;
    }

    @Override
    public Object getKey() {
        return meta;
    }

    @Override
    public String asTemplateContent() {
        return ""; // the meta data is fetched via AutoToString dialect
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
        return "";
    }

    @Override
    public String getSuggestedClassName() {
        try {
            return "C" + UUID.nameUUIDFromBytes(meta.toString().getBytes("utf-8")).toString().replace('-', '_');
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ToStringTemplateResource) {
            ToStringTemplateResource that = (ToStringTemplateResource)obj;
            return that.getKey().equals(getKey());
        }
        return false;
    }
}
