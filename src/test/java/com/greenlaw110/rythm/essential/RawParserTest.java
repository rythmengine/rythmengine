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
 * Test @raw()
 */
public class RawParserTest extends TestBase {

    /**
     * Test default @raw()
     */
    @Test
    public void testDefault() {
        t = "@raw(){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("<h1>abc</h1>");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@raw(){\n123\n}\nxyz";
        s = r(t);
        eq("abc\n123\nxyz");
    }
    
    @Test
    public void testShortNotation() {
        t = "@raw()@1@";
        s = r(t, "<h1>h1</h1>");
        eq("<h1>h1</h1>");
    }

    public static void main(String[] args) {
        run(RawParserTest.class);
    }
}
