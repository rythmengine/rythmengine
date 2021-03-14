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

import java.util.Locale;

/**
 * Created by luog on 2/01/14.
 */
public interface IFormatter {
    /**
     * Try to format the object. If the formatter does not recongize the object, then
     * {@code null} shall be returned immediately
     *
     * @param val the value object to be formatted
     * @param pattern the pattern to format the object
     * @param locale current locale
     * @param timezone current timezone
     * @return the formatted string from the value
     */
    String format(Object val, String pattern, Locale locale, String timezone);
}
