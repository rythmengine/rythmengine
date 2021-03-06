/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.utils;

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

import java.io.Serializable;

/**
 * Used for escaping
 */
public class RawData implements Serializable {
    private static final long serialVersionUID = 1448378182708583237L;
    public String data;

    public RawData(Object val) {
        if (val == null) {
            data = "";
        } else {
            data = val.toString();
        }
    }
    
    public RawData append(Object val) {
        StringBuilder sb = new StringBuilder(data).append(val);
        return new RawData(sb);
    }

    public RawData appendTo(Object val) {
        StringBuilder sb = new StringBuilder(S.str(val)).append(data);
        return new RawData(sb);
    }

    @Override
    public String toString() {
        return data;
    }
    
    public static RawData valueOf(Object o) {
        return new RawData(o);
    }

    public static final RawData NULL = new RawData(null);
}
