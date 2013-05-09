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
 * Test @compact() parser
 */
public class NoCompactParserTest extends TestBase {
    
    @Test
    public void test() {
        t = "<h1> abc   </h1>";
        eq(t);
        
        t = "@noCompact(){<h1> abc   </h1>}";
        s = r(t);
        eq("<h1> abc   </h1>");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc@noCompact(){1  2  3}xyz";
        eq("abc1  2  3xyz");
        
        t = "abc\n@noCompact(){1  2  3}\nxyz";
        s = r(t);
        eq("abc\n1  2  3\nxyz");
        
        t = "abc\n@noCompact(){\n1 2   3\n}\nxyz";
        s = r(t);
        eq("abc\n1 2   3\nxyz");
        
        t = "abc\n  @noCompact(){\n1 2   3\n}\nxyz";
        s = r(t);
        eq("abc\n  1 2   3\nxyz");
    }

    @Test
    public void testShortNotation() {
        t = "@noCompact()1 2   3@";
        s = r(t);
        eq("1 2   3");
    }
    
    @Test
    public void testWithCompact() {
        t = "@noCompact(){@compact(){1 2    3} x  y   z}";
        s = r(t);
        eq("1 2 3 x  y   z");
    }
    
    public static void main(String[] args) {
        run(NoCompactParserTest.class);
    }
    
}
