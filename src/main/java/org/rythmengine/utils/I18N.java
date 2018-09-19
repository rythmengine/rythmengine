/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.utils;

import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.internal.CacheKey;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.template.ITemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * I18N utils
 */
// Most of the code come from Play!Framework I18N.java, under Apache License 2.0
public class I18N {

    private static final ILogger logger = Logger.get(I18N.class);

    private I18N() {
    }

    /**
     * Return a {@link org.rythmengine.template.ITemplate template}'s current locale, or
     * the the {@link org.rythmengine.RythmEngine#get() current engine}'s 
     * {@link org.rythmengine.conf.RythmConfigurationKey#I18N_LOCALE locale configuration}
     * or finally the default locale with {@link java.util.Locale#getDefault() java.util.Locale.getDefault()}
     * call
     * 
     * @param template the template being executing
     * @return a locale instance
     */
    public static Locale locale(ITemplate template) {
        if (null != template) {
            return template.__curLocale();
        }
        Locale retval;
        RythmEngine engine = RythmEngine.get();
        if (null != engine) {
            retval = engine.conf().locale();
        } else {
            retval = RythmConfigurationKey.I18N_LOCALE.getDefaultConfiguration();
        }
        return retval;
    }

    public static Locale locale() {
        return locale(null);
    }

    public static ResourceBundle bundle(String name) {
        return bundle(null, name, null);
    }

    private static final Map<String, ResourceBundle> bundleCache = new HashMap<String, ResourceBundle>();

    public static ResourceBundle bundle(ITemplate template, String name, Locale locale) {
        if (null == name) throw new NullPointerException();
        String cacheKey = null;
        ResourceBundle retval = null;
        if (null == locale) {
            locale = locale(template);
        }
        RythmEngine engine = null == template ? RythmEngine.get() : template.__engine(); 
        if (null != engine && null != locale) {
            cacheKey = CacheKey.i18nBundle(engine, locale);
            retval = bundleCache.get(cacheKey);
        }

        String charset=  engine.conf().get("i18n.message.sources.charset").toString();
        CharsetControl control = new CharsetControl(charset);
        
        if (null == retval) {
            try {
                if (null == locale) locale = RythmConfigurationKey.I18N_LOCALE.getDefaultConfiguration();
                retval = ResourceBundle.getBundle(name, locale, control);
                bundleCache.put(cacheKey, retval);
            } catch (RuntimeException e) {
                logger.warn(e, "Error getting resource bundle by name %s", name);
            }
        }
        return retval;
    }

}
