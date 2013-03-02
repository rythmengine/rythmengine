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
package com.greenlaw110.rythm.render_mode.substitute;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

import static com.greenlaw110.rythm.utils.NamedParams.*;

/**
 * Test rythm toString and autoToString
 */
public class SubstituteTest extends TestBase {

    public SubstituteTest() {
    }


    @Test
    public void testGoodTemplate() {
        String t = "Hello @who!";
        String p = "Rythm";
        String s = Rythm.substitute(t, from(p("who", p)));
        assertEquals("Hello Rythm!", s);
    }
    
    @Test(expected = Exception.class)
    public void testBadTemplate() {
        String t = "@(who)'s length is @who.length()";
        String p = "Rythm";
        Rythm.substitute(t, from(p("who", p)));
    }
    
    public static void main(String[] args) {
        run(SubstituteTest.class);
    }
}
