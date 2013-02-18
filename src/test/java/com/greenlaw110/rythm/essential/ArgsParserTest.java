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
package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

import java.util.Collections;

import static com.greenlaw110.rythm.conf.RythmConfigurationKey.ENGINE_OUTPUT_JAVA_SOURCE_ENABLED;

/**
 * Test @args parser
 */
public class ArgsParserTest extends TestBase {
    @Test
    public void testSimple() {
        t = "@args String s = \"\", int i\ns:@s.getClass().getName(),i:@i.getClass().getName()";
        s = r(t);
        eq("s:java.lang.String,i:java.lang.Integer");
    }
    
    @Test
    public void testArray() {
        t = "@args int[] a1, String[] a2\na1:@a1.getClass().isArray(),a2:@a2.getClass().isArray()";
        // note cannnot use new int[]{} here
        s = r(t, new Integer[]{}, new String[]{});
        eq("a1:true,a2:true");
    }
    
    @Test
    public void testFullFormat() {
        t = "@args() {Map<String, String> m, List<int> l}@(m instanceof Map),@(l instanceof List)";
        s = r(t, Collections.EMPTY_MAP, Collections.EMPTY_LIST);
        eq("true,true");
    }
    
    @Test
    public void testDefaultValue() {
        t = "@args() {String s = \"foo\", int i = 3}@s @i";
        s = r(t);
        eq("foo 3");
    }
    
    @Test
    public void testPositionHolder() {
        t = "@args String @1, int @2;@(1)_@2";
        s = r(t, "s", 0);
        eq("s_0");
    }
    
    @Test
    public void testLineBreaks() {
        t = "abc\n@args String @1\nxyz@1";
        s = r(t, "s");
        eq("abc\nxyzs");
        t = "abc@args(){String @1}xyz@1";
        s = r(t, "s");
        eq("abcxyzs");
        t = "abc\n@args(){String @1}\nxyz@1";
        s = r(t, "s");
        eq("abc\nxyzs");
        t = "abc\n@args(){\nString @1\n}\nxyz@1";
        s = r(t, "s");
        eq("abc\nxyzs");
        t = "abc\n\t@args(){\nString @1\n}\nxyz@1";
        s = r(t, "s");
        eq("abc\nxyzs");
    }

    public static void main(String[] args) {
        run(ArgsParserTest.class);
    }
}
