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
package com.greenlaw110.rythm.layout;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test layout handling
 */
public class LayoutTest extends TestBase {
    @Test
    public void test() {
        s = r("foo/index.html");
        eqf("foo/index.result");
    }
    
    @Test
    public void testSet() {
        s = r("foo/index2.html");
        eqf("foo/index2.result");
    }
    
    @Test
    public void testExtendArgs() {
        s = r("foo/index3.html");
        eqf("foo/index3.result");

        s = r("foo/index4.html");
        eqf("foo/index4.result");
    }
    
    @Test
    public void testRenderSection() {
        s = r("foo/index5.html");
        eqf("foo/index5.result");
    }
    
    @Test
    public void testRenderSectionWithDefaultContent() {
        s = r("foo/index6.html");
        eqf("foo/index6.result");
    }
    
    @Test
    public void testExtendsTemplateInCurDir() {
        s = r("foo/index7.html");
        eqf("foo/index7.result");
    }
    
    @Test
    public void testExtendsTemplateInImportPaths() {
        s = r("foo/index8.html");
        eqf("foo/index8.result");
    }
    
    @Test
    public void testInit() {
        s = r("foo/index9.html");
        eqf("foo/index9.result");
    }

    public static void main(String[] args) {
        run(LayoutTest.class);
    }
}
