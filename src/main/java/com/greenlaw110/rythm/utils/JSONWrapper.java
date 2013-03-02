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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Define a type to wrap a JSON string
 */
public class JSONWrapper {
    private JSON j_;

    /**
     * Construct a JSONWrapper with a JSON string. If the string is not valid JSON, then
     * a RuntimeException will thrown out
     *
     * @param str
     */
    public JSONWrapper(String str) {
        if (S.empty(str)) throw new IllegalArgumentException("empty json str");
        Object o;
        try {
            o = JSON.parse(str);
        } catch (com.alibaba.fastjson.JSONException e) {
            throw new RuntimeException("Invalid JSON string: " + str);
        }
        if (o instanceof JSON) {
            j_ = (JSON) o;
        } else {
            throw new RuntimeException("JSON string parse to unknown object type: " + o.getClass());
        }
    }

    /**
     * Return true if the underline JSON data is an array
     *
     * @return true if the JSON data is an array
     */
    public boolean isArray() {
        return j_ instanceof JSONArray;
    }

    /**
     * Return a List of object contained in the JSON array.
     * <p/>
     * <p></p>If the data is not a JSON array then a ClassCastException will
     * be thrown out</p>
     *
     * @return the List of objects
     */
    public List<Object> getArray() {
        return (JSONArray) j_;
    }

    /**
     * Return a Map of String and object contained in the JSON object.
     * <p/>
     * <p></p>If the data is a JSON array then a ClassCastException will
     * be thrown out</p>
     *
     * @return the mapped json attributes
     */
    public Map<String, Object> getObject() {
        return (JSONObject) j_;
    }

    /**
     * Parse the string and return the JSONWrapper
     * @param s
     * @return JSON wrapper of the string
     */
    public static JSONWrapper wrap(String s) {
        return new JSONWrapper(s);
    }
}
