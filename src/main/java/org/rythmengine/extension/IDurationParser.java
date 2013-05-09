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
package org.rythmengine.extension;

import org.rythmengine.utils.Time;

/**
 * A user application could implement this interface to provide
 * customized time string parsing utility to {@link ICacheService rythm cache service}.
 * and then configure the Rythm to use customized implementation via
 * {@link org.rythmengine.conf.RythmConfigurationKey#CACHE_DURATION_PARSER_IMPL "cache.duration_parser.impl"}
 * configuration.
 * <p>Usually user application does not need to provide it's own implementation, instead, the rythm built in time
 * parser could be used as default implementation</p>
 */
public interface IDurationParser {

    /**
     * Parse a string representation and return number of seconds
     *
     * @param s
     * @return duration in seconds
     */
    int parseDuration(String s);

    /**
     * Rythm's default implementation of {@link IDurationParser}. It allows the following type of duration string
     * representations:
     * <ul>
     * <li>1d: 1 day</li>
     * <li>3h: 3 hours</li>
     * <li>8mn or 8min: 8 minutes</li>
     * <li>23s: 23 seconds</li>
     * </ul>
     */
    public static final IDurationParser DEFAULT_PARSER = new IDurationParser() {
        @Override
        public int parseDuration(String s) {
            return Time.parseDuration(s);
        }
    };
}
