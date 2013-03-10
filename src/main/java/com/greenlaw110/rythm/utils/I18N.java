/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.internal.CacheKey;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.template.TemplateBase;

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

    public static Locale locale(RythmEngine engine) {
        Locale retval;
        if (null == engine) {
            engine = RythmEngine.get();
        }
        if (null != engine) {
            TemplateBase template = engine.currentTemplate();
            if (null != template) {
                retval = template.__curLocale();
            } else {
                retval = engine.conf().locale();
            }
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

    public static ResourceBundle bundle(RythmEngine engine, String name, Locale locale) {
        if (null == name) throw new NullPointerException();
        String cacheKey = null;
        ResourceBundle retval = null;
        if (null == locale) {
            if (null == engine) {
                engine = RythmEngine.get();
            }
            if (null != engine) {
                locale = locale(engine);
            }
        } else if (null == engine) {
            engine = RythmEngine.get();
        }
        if (null != engine && null != locale) {
            cacheKey = CacheKey.i18nBundle(engine, locale);
            retval = bundleCache.get(cacheKey);
        }

        if (null == retval) {
            try {
                if (null == locale) locale = RythmConfigurationKey.I18N_LOCALE.getDefaultConfiguration();
                retval = ResourceBundle.getBundle(name, locale);
                bundleCache.put(cacheKey, retval);
            } catch (RuntimeException e) {
                logger.warn(e, "Error getting resource bundle by name %s", name);
            }
        }
        return retval;
    }

}
