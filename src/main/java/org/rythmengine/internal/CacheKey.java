/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
