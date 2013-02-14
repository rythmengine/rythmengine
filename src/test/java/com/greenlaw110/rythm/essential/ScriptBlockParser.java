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

/**
 * Test scripting block parser
 */
public class ScriptBlockParser extends TestBase {
    @Test
    public void test() {
        t = "abc\n@{\n\tint i = 0;\n\tint j = 1;\n}\ni + j = @(i + j)";
        s = r(t);
        assertEquals("abc\ni + j = 1", s);
    }
    
    @Test
    public void testInline() {
        t = "abc@{\n\tint i = 0;\n\tint j = 1;\n}i + j = @(i + j)";
        eq("abci + j = 1");
    }
    
    @Test
    public void testHalfInline() {
        t = "abc@{\n\tint i = 0;\n\tint j = 1;\n}\ni + j = @(i + j)";
        eq("abc\ni + j = 1");
    }

    @Test
    public void testHalfInline2() {
        // this one won't work due to Rythm limit. Fix me!
//        t = "abc\n@{\n\tint i = 0;\n\tint j = 1;\n}i + j = @(i + j)";
//        eq("abc\ni + j = 1");
    }

}
