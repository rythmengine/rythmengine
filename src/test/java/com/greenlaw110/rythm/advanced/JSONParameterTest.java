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
package com.greenlaw110.rythm.advanced;

import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.utils.JSONWrapper;
import org.junit.Test;

/**
 * Test passing JSON string as template parameter
 */
public class JSONParameterTest extends TestBase {

    public static class User {
        public String name;
        public int age;
    }
    
    @Test
    public void testSimple() {
        t = "@args String name;hello @name";
        s = r(t, JSONWrapper.wrap("{\"name\":\"world\"}"));
        eq("hello world");
        
        String s0 = "{\"name\":\"\\\"world\\\"\"}";
        s = r(t, JSONWrapper.wrap(s0));
        eq("hello \"world\"");
    }
    
    @Test
    public void testArray() {
        t = "@args List<com.greenlaw110.rythm.advanced.JSONParameterTest.User> users\n<ul>@for(users){\n@_.name: @_.age\n}</ul>";
        String params = "{users: [{\"name\":\"\\\"Tom\\\"\", \"age\": 12}, {\"name\":\"Peter\", \"age\": 11}]}";
        s = r(t, JSONWrapper.wrap(params));
        eq("<ul>\n\"Tom\": 12\nPeter: 11\n</ul>");
    }
    
    @Test
    public void testArray2() {
        t = "@args List<com.greenlaw110.rythm.advanced.JSONParameterTest.User> users\n<ul>@for(users){\n@_.name: @_.age\n}</ul>";
        System.out.println(t);
        String params = "[{\"name\":\"Tom\", \"age\": 12}, {\"name\":\"Peter\", \"age\": 11}]";
        s = r(t, JSONWrapper.wrap(params));
        eq("<ul>\nTom: 12\nPeter: 11\n</ul>");
    }
    
    @Test
    public void testObject() {
        t = "@args com.greenlaw110.rythm.advanced.JSONParameterTest.User user\n@user.name: @user.age";
        String params = "{user: {\"name\":\"Tom\", \"age\": 12}}";
        s = r(t, JSONWrapper.wrap(params));
        eq("Tom: 12");
    }

    public static void main(String[] args) {
        run(JSONParameterTest.class);
    }
}
