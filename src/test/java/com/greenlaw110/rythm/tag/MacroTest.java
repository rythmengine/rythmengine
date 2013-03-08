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
package com.greenlaw110.rythm.tag;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test macro definition and invocation
 */
public class MacroTest extends TestBase {
    @Test
    public void testSimpleExec() {
        t = "@macro(foo){bar}@exec(foo)";
        s = r(t);
        eq("bar");
    }
    
    @Test
    public void testShortNotation() {
        t = "@macro(foo)zoo@ @exec(foo)";
        s = r(t);
        eq("zoo");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@macro(foo){\nbar\n}\n@exec(foo)";
        s = r(t);
        eq("abc\nbar");
    }
    
    @Test
    public void testInvokeMacro() {
        t = "@macro(foo){bar}@foo()";
        s = r(t);
        eq("bar");
    }
    
    @Test
    public void testExpand() {
        t = "@macro(foo){bar}@expand(foo)";
        s = r(t);
        eq("bar");
    }

    public static void main(String[] args) {
        run(MacroTest.class);
    }
}
