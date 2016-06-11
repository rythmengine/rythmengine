/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

import org.rythmengine.RythmEngine;
import org.rythmengine.template.ITemplate;
import org.rythmengine.utils.I18N;

import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/03/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class CacheKey {
    private CacheKey() {
    }

    public static String i18nMsg(ITemplate template, String key, boolean useFormat) {
        return i18nMsg(template, key, useFormat, null);
    }
    
    public static String i18nMsg(ITemplate template, Object key, boolean useFormat, Locale locale) {
        if (null == template) return "";
        if (null == locale) {
            locale = I18N.locale();
        }
        return String.format("%s-i18nM-%s-%s-%s", key, locale, useFormat, template.__engine());
    }
    
    public static String i18nBundle(RythmEngine engine, Locale locale) {
        return String.format("i18nB-%s-%s", locale, engine);
    }
}
