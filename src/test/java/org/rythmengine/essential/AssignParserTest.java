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

import org.junit.Test;
import org.rythmengine.TestBase;

/**
 * Test @assign parser
 */
public class AssignParserTest extends TestBase {
    @Test
    public void test() {
        t = "@assign(x){abc}@x";
        s = r(t);
        eq("abc");
        
        t = "@assign(x)abc@ @x";
        s = r(t);
        eq("abc");
    }
    
    @Test
    public void testLineBreaks() {
        t = "abc\n@assign(x){abc}\n@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\nabc\nxyz");

        t = "abc\n@assign(x){\n\tabc\n\txyz}\n@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\n\tabc\n\txyz\nxyz");

        t = "abc\n@assign(x){\n\tabc\n}\n@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\n\tabc\nxyz");

        t = "abc\n@assign(x){abc}@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\nabc\nxyz");
    }
    
    @Test
    public void testShortNotation() {
        t = "@assign(x)abc@\n@x";
        s = r(t);
        eq("abc");
    }

    public static void main(String[] args) {
        run(AssignParserTest.class);
    }
}
