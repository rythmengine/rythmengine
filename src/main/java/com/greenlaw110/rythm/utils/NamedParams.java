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

import com.greenlaw110.rythm.Rythm;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class help to construct named value map
 */
public class NamedParams {

    private NamedParams() {
    }

    public static final NamedParams instance = new NamedParams();

    public static class Pair {
        public String k;
        public Object v;

        public Pair(String key, Object val) {
            k = key;
            v = val;
        }
    }

    public static Pair pair(String k, Object v) {
        return new Pair(k, v);
    }

    public static Pair p(String k, Object v) {
        return new Pair(k, v);
    }

    public static Map<String, Object> from(Pair... pairs) {
        Map<String, Object> map = new HashMap<String, Object>(pairs.length);
        for (Pair p : pairs) {
            map.put(p.k, p.v);
        }
        return map;
    }

    public static void main(String[] args) {
        System.out.println(Rythm.render("green@rythmengine.com"));
    }
}
