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

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class help to construct named value map
 */
public class NamedParams {

	  /**
	   * force static usage only
	   */
    private NamedParams() {
    }

    // could be a singleton but isn't at this time
    // public static final NamedParams instance = new NamedParams();

    /** 
     * Helper class that holds name/value pairs to be used
     * for a template rootmap
     */
    public static class Pair {
        String key;
        Object value;

        /**
         * construct me from a key value pair
         * @param pKey
         * @param pValue
         */
        public Pair(String pKey, Object pValue) {
            key = pKey;
            value = pValue;
        }
    }

    /**
     * create a pair from the given key and value
     * @param key
     * @param value
     * @return
     */
    public static Pair p(String key, Object value) {
        return new Pair(key, value);
    }

    /**
     * construct me from the given pairs
     * @param pairs
     * @return the map
     */
    public static Map<String, Object> from(Pair... pairs) {
        Map<String, Object> map = new HashMap<String, Object>(pairs.length);
        for (Pair p : pairs) {
            map.put(p.key, p.value);
        }
        return map;
    }

}
