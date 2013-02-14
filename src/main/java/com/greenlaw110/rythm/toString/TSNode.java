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
package com.greenlaw110.rythm.toString;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A TSNode (ToString Node) represents an object to be put into the toString stream
 */
public class TSNode {
    public Class<?> type = null;
    public Map<String, String> expressions = new HashMap<String, String>();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof TSNode) {
            return ((TSNode) obj).type.equals(type);
        }
        return false;
    }

    public static TSNode parseClass(Class<?> clazz) {
        Field f = null;
        Class c = f.getType();
        c.getComponentType();
        Type t = f.getGenericType();
        //t.
        return null;
    }
}
