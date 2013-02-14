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

import com.greenlaw110.rythm.TestBase;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

import com.greenlaw110.rythm.exception.CompileException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test type inference
 */
public class TypeInferenceTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
    }

    @Test
    public void test() {
        t = "@1.size() and @2.size()";
        Map m1 = new HashMap();
        m1.put("foo", "bar");
        Map m2 = new HashMap();
        s = r(t, m1, m2);
        assertEquals("1 and 0", s);
    }
    
    @Test(expected = CompileException.class)
    public void testFeatureDisabled() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "false");
        t = "@1.size() and @2.size()";
        Map m1 = new HashMap();
        m1.put("foo", "bar");
        Map m2 = new HashMap();
        s = r(t, m1, m2);
        assertEquals("1 and 0", s);
    }
    
    @Test()
    public void testCallingWithDifferentType() {
        t = "@1.size() and @2.size()";
        Map m1 = new HashMap();
        m1.put("foo", "bar");
        Map m2 = new HashMap();
        s = r(t, m1, m2);
        assertEquals("1 and 0", s);

        List l1 = new ArrayList();
        l1.add("xx");
        List l2 = new ArrayList();
        s = r(t, l1, l2);
        assertEquals("1 and 0", s);
    }

    public static void main(String[] args) {
        run(TypeInferenceTest.class);
    }
}
