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
package org.rythmengine.tag;

import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test inline tag define and invocation
 */
public class InlineTagTest extends TestBase {
    @Test
    public void testSimple() {
        t = "@def foo(String s){foo on @s}@foo(\"bar\")";
        s = r(t);
        eq("foo on bar");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@def foo(String s){\nfoo on @s\n}\n@foo(\"bar\")";
        s = r(t);
        eq("abc\nfoo on bar");
    }
    
    @Test
    public void testWithReturnValue() {
        t = "@def int dbl(int i){return i * 2;}@dbl(2)";
        s = r(t);
        eq("4");
    }
    
    @Test
    public void testShortNotation() {
        t = "@def int dbl(int i) return i * 2;@ @dbl(5)";
        s = r(t);
        eq("10");
    }

    @Test
    public void innerClass() {
        t = "@def class Foo {String bar() {return \"bar\";}}@(new Foo().bar())";
        s = r(t);
        eq("bar");
    }
    
    @Test
    public void testAlias() {
        t = "@tag int dbl(int i) return i * 2;@ @dbl(5)";
        s = r(t);
        eq("10");

        t = "@tag int dbl(int i){return i * 2;}@dbl(2)";
        s = r(t);
        eq("4");

        t = "abc\n@tag foo(String s){\nfoo on @s\n}\n@foo(\"bar\")";
        s = r(t);
        eq("abc\nfoo on bar");
    }

    public static void main(String[] args) {
        run(InlineTagTest.class);
    }
    
}
