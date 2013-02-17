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
package com.greenlaw110.rythm.advanced;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import org.junit.Before;
import org.junit.Test;
import static com.greenlaw110.rythm.utils.NamedParams.*;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

/**
 * Test Natural Template feature
 */
public class NaturalTemplateTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Rythm.shutdown();
    }
    
    @Test
    public void testBasicDirectives() {
        
        String t = "<!-- @args String name, int age -->Hi @name, happy @age!";
        String s = Rythm.render(t, from(p("name", "Green Luo"), p("age", 100)));
        assertEquals("Hi Green Luo, happy 100!", s);
        
        t = "<!-- @if (false) {-->false<!-- } else {-->true<!-- }-->";
        s = Rythm.render(t);
        assertEquals("true", s);
        
        t = "<!-- @for (1..5) {-->@(_)@_sep<!-- } -->";
        s = Rythm.render(t);
        assertEquals("1,2,3,4", s);
    }
    
    @Test
    public void testComments() {
        String t = "<!-- @// abcde -->";
        String s = Rythm.render(t);
        assertEquals("", s);
        
        t = "<!-- @* abc\nxyz@abcd\n*@ -->";
        s = Rythm.render(t);
        assertEquals("", s);
    }
    
    @Test
    public void testLangSwitch() {
        String t = "<!-- @if (true) { --><script>/* @if(true) {*/alert(1);<!-- } --></script><!-- } -->";
        String s = Rythm.render(t);
        System.setProperty(ENGINE_OUTPUT_JAVA_SOURCE_ENABLED.getKey(), "false");
        assertEquals("<script>alert(1);</script>", s);
    }
    
    @Test
    public void testFeatureDisabled() {
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "false");
        String t = "<!-- @args String name, int age -->Hi @name, happy @age!";
        String s = Rythm.render(t, from(p("name", "Green Luo"), p("age", 100)));
        assertEquals("<!-- -->Hi Green Luo, happy 100!", s);
        
        t = "<!-- @if (false) {-->false<!-- } else {-->true<!-- }-->";
        s = Rythm.render(t);
        assertEquals("<!-- -->true<!-- -->", s);
        
        t = "<!-- @for (1..5) {-->@(_)@_sep<!-- } -->";
        s = Rythm.render(t);
        assertEquals("<!-- -->1,<!-- -->2,<!-- -->3,<!-- -->4<!--  -->", s);
    }

    public static void main(String[] args) {
        run(NaturalTemplateTest.class);
    }
}
