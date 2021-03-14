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

import org.rythmengine.utils.RawData;
import org.rythmengine.utils.S;

/**
 * Used to help track loop state
 */
public class LoopUtil {
    private final Object obj;
    private final boolean isFirst;
    private final boolean isLast;

    public LoopUtil(boolean isFirst, boolean isLast) {
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.obj = null;
    }
    
    public LoopUtil(boolean isFirst, boolean isLast, Object obj) {
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.obj = obj;
    }

    public RawData sep(String sep) {
        return postSep(sep);
    }

    public RawData preSep(String sep) {
        String result = "";
        if (null != obj) {
            result += S.escape(obj);
        }
        return RawData.valueOf(result);
    }

    public RawData postSep(String sep) {
        String result = "";
        if (null != obj) {
            result += S.escape(obj);
        }
        return RawData.valueOf(result + (isLast ? "" : sep));
    }
}
