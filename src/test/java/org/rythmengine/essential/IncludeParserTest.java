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

import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test include parser
 */
public class IncludeParserTest extends TestBase {
    @Test
    public void test() {
        s = r("foo/includeTest.html");
        eqf("foo/includeTest.result");
    }
    
    @Test
    public void testIncludeInlineFunction() {
        s = r("foo/includeInlineFunction.html");
        eqf("foo/includeInlineFunction.result");
    }
    
    @Test
    public void testIncludeInLayoutTemplate() {
        s = r("foo/includeInLayoutTemplate.html");
        eqf("foo/includeInLayoutTemplate.result");
    }

    public static void main(String[] args) {
        run(IncludeParserTest.class);
    }
}
