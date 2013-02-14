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
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;
import static com.greenlaw110.rythm.utils.NamedParams.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Test @if parser
 */
public class IfParserTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
    }

    @Test
    public void testIf() {
        t = "@if(true) {true}";
        s = r(t);
        assertEquals("true", s);
        
        t = "@if(true) true@";
        assertEquals("true", s);
    }
    
    @Test
    public void testIfNot() {
        t = "@ifNot(false) {true}";
        s = r(t);
        assertEquals("true", s);
        
        t = "@ifNot(true) true@";
        s = r(t);
        assertEquals("", s);
    }
    
    @Test
    public void testIfElse() {
        t = "@if(@1) {true} else {false}";
        s = r(t, true);
        assertEquals("true", s);
        s = r(t, false);
        assertEquals("false", s);
        
        t = "@if(@1) true@ else false@";
        s = r(t, true);
        assertEquals("true", s);
        s = r(t, false);
        assertEquals("false", s);
    }

    @Test
    public void testIfNotElse() {
        t = "@ifNot(@1) {true} else {false}";
        s = r(t, true);
        assertEquals("false", s);
        s = r(t, false);
        assertEquals("true", s);
        
        t = "@ifNot(@1) true@ else false@";
        s = r(t, true);
        assertEquals("false", s);
        s = r(t, false);
        assertEquals("true", s);
    }
    
    @Test
    public void testIfElseIf() {
        t = "@if(@1 < 14) {kid} else if (@1 < 30) {yong man} else {aged}";
        s = r(t, 10);
        assertEquals("kid", s);
        s = r(t, 28);
        assertEquals("yong man", s);
        s = r(t, 200);
        assertEquals("aged", s);

        t = "@if(@1 < 14) kid@ else if (@1 < 30) yong man@ else aged@";
        s = r(t, 10);
        assertEquals("kid", s);
        s = r(t, 28);
        assertEquals("yong man", s);
        s = r(t, 200);
        assertEquals("aged", s);
    }
    
    @Test
    public void testIfWithLineBreak() {
        t = "abc\n@if(@1 < 14) {\n\tkid\n} else if (@1 < 30) {\n\tyong man\n} else {\n\taged\n} \nabc";
        s = r(t, 10);
        assertEquals("abc\n\tkid\nabc", s);
        s = r(t, 28);
        assertEquals("abc\n\tyong man\nabc", s);
        s = r(t, 200);
        assertEquals("abc\n\taged\nabc", s);
        
        t = "@if(@1 < 14) \n\tkid\n@ else if (@1 < 30) \n\tyong man\n@ else \n\taged\n@\n 123";
        s = r(t, 10);
        assertEquals("\tkid\n 123", s);
        s = r(t, 28);
        assertEquals("\tyong man\n 123", s);
        s = r(t, 200);
        assertEquals("\taged\n 123", s);
    }
    
    private void yes(Object p) {
        assertEquals("yes", r(t, p, null));
    }
    
    private void no(Object p) {
        assertEquals("no", r(t, p, null));
    }
    
    @Test
    public void testSmartIfEval() {
        t = "@if(@1){yes}else{no}";
        yes("whatever string");
        no(null);
        no("FALSE");
        no("NO");
        yes("yes");
        no("  ");
        no(Collections.EMPTY_LIST);
        no(Collections.EMPTY_MAP);
        no(Collections.EMPTY_SET);
        yes(Arrays.asList("1,2".split(",")));
        yes(1);
        no(0L);
        yes(1.1);
        no((char)0);
        String[] sa = new String[0];
        no(sa);
        Integer[] ia = {1}; //note cannot use int[] here, it will cause type cast exception
        yes(ia);
    }
    
    public static void main(String[] args) {
        run(IfParserTest.class);
    }
}
