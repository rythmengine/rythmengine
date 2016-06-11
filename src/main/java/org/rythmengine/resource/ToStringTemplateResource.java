/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.resource;

import org.rythmengine.internal.dialect.AutoToString;

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

    private static final long serialVersionUID = -1428821795298562604L;
    private AutoToString.AutoToStringData meta;

    public ToStringTemplateResource(AutoToString.AutoToStringData data) {
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
}
