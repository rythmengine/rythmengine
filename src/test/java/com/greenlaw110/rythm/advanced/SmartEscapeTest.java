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
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.extension.ICodeType;
import org.junit.Before;
import org.junit.Test;

import static com.greenlaw110.rythm.conf.RythmConfigurationKey.FEATURE_SMART_ESCAPE_ENABLED;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;

/**
 * Test Smart Escape feature
 */
public class SmartEscapeTest extends TestBase {
    
    @Before
    public void setup() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "true");
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
    }
    
    @Test
    public void test() {
        String p1 = "<h1>h1</h1>";
        String p2 = "tom's store";
        String t = "@1<script>alert('@2');</script>";
        String s = Rythm.render(t, p1, p2);
        assertEquals("&lt;h1&gt;h1&lt;/h1&gt;<script>alert('tom\\'s store');</script>", s);
    }
    
    @Test
    public void testFeatureDisabled() {
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "false");
        String p1 = "<h1>h1</h1>";
        String p2 = "tom's store";
        String t = "@1<script>alert('@2');</script>";
        String s = Rythm.render(t, p1, p2);
        assertEquals("&lt;h1&gt;h1&lt;/h1&gt;<script>alert('tom&apos;s store');</script>", s);
    }
    
    public static void main(String[] args) {
        run(SmartEscapeTest.class);
    }
}
