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
 * test @invoke()
 */
public class InvokeParserTest extends TestBase {

    @Test
    public void test() {
        t = "@invoke(\"bar.included\")";
        s = r(t);
        eq("included content");
    }

    @Test
    public void testLineBreak() {
        t = "abc\n\t@invoke(\"bar.included\")\n123";
        s = r(t);
        eq("abc\n\tincluded content\n123");
    }

    @Test
    public void testWithParameter() {
        t = "@invoke(\"foo.zee.x\", \"ss\", 123)";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test
    public void testPassParamByName() {
        t = "@invoke(\"foo.zee.x\", y: 123, x = \"ss\")";
        s = r(t);
        eq("x = ss, y = 123");

        t = "@invoke(\"foo.zee.x\", {y: 123, \n\tx = \"ss\"})";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test(expected = RuntimeException.class)
    public void testNonExisting() {
        t = "@invoke(\"non.exists\")";
        s = r(t);
        eq("");
    }
    
    @Test
    public void testNonExistingWithIgnore() {
        t = "@invoke(\"non.exists\").ignoreNonExistsTag()";
        s = r(t);
        eq("");
    }

    public static void main(String[] args) {
        run(InvokeParserTest.class);
    }
}
