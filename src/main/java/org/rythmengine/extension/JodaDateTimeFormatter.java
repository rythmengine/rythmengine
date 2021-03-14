/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by luog on 2/01/14.
 */
public class JodaDateTimeFormatter implements IFormatter {
    @Override
    public String format(Object val, String pattern, Locale locale, String timezone) {
        if (!(val instanceof DateTime)) return null;
        DateTimeFormatter fmt;
        if (null != pattern) fmt = DateTimeFormat.forPattern(pattern);
        else fmt = DateTimeFormat.fullDateTime();

        fmt = fmt.withLocale(locale);
        if (null != timezone) {
            DateTimeZone dtz = DateTimeZone.forID(timezone);
            fmt = fmt.withZone(dtz);
        }
        return fmt.print((DateTime)val);
    }
}
