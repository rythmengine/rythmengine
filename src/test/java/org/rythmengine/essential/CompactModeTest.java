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
package org.rythmengine.essential;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.junit.Before;
import org.junit.Test;

import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_COMPACT_ENABLED;

public class CompactModeTest extends TestBase {
    
    @Before
    public void setup() {
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "true");
    }
    
    @Test
    public void testCompactSpace() {
        t = "<h1>   abc</h1>  x";
        eq("<h1> abc</h1> x");
    }
    
    @Test
    public void testLineBreaks() {
        t = "<h1>\n\nabc\n</h1>";
        eq("<h1>\nabc\n</h1>");
    }
    
    @Test
    public void testBlankAfterLineBreak() {
        t = "<h1> \n</h1>";
        eq("<h1>\n</h1>");
    }
    
    @Test
    public void testLineBreakAfterBlank() {
        t = "<h1>\n </h1>";
        eq("<h1>\n</h1>");
    }
    
    @Test
    public void testCompactDisabled() {
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        t = "<h1>   abc</h1>  x";
        eq("<h1>   abc</h1>  x");

        t = "<h1>\n\nabc\n</h1>";
        s = r(t);
        eq("<h1>\n\nabc\n</h1>");
    }

    @Test
    public void testFile() {
        s = r("foo/compact_test.html", "Rythm");
        eqf("foo/compact_test_compact.result");
        Rythm.shutdown();
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        s = r("foo/compact_test.html", "Rythm");
        eqf("foo/compact_test_no_compact.result");
    }

    public static void main(String[] args) {
        run(CompactModeTest.class);
    }
}
