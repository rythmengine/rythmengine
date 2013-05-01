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
import com.greenlaw110.rythm.utils.S;
import org.junit.Test;

import java.util.*;

import static com.greenlaw110.rythm.utils.Eval.eval;

/**
 * Test utilities
 */
public class UtilsTest extends TestBase {
    @Test
    public void testTS() {
        long now = System.currentTimeMillis();
        t = "@ts()";
        s = r(t);
        long l = Long.valueOf(s);
        assertTrue(l > now);
        System.out.println(l - now);
        assertTrue((l - now) < 4000);
    }
    
    private void f(Boolean b) {
        assertFalse(b);
    }
    
    private void t(Boolean b) {
        assertTrue(b);
    }
    
    Object o(Object o) {
        return o;
    }
    
    @Test
    public void testEval() {
        String s = "";
        f(eval(s));
        f(eval(o(s)));
        
        s = " ";
        f(eval(s));
        f(eval(o(s)));
        
        s = "foo";
        t(eval(s));
        t(eval(o(s)));
        
        s = "false";
        f(eval(s));
        f(eval(o(s)));
        
        s = "no";
        f(eval(s));
        f(eval(o(s)));
        
        int i = 0;
        f(eval(i));
        f(eval(o(i)));
        
        i = 1;
        t(eval(i));
        t(eval(o(i)));
        
        double d = 0.00000000001D;
        f(eval(d));
        f(eval(o(d)));
        
        d = 0.121D;
        t(eval(d));
        t(eval(o(d)));
        
        Map m = Collections.emptyMap();
        f(eval(m));
        f(eval(o(m)));
        
        m = new HashMap();
        m.put("x", null);
        t(eval(m));
        t(eval(o(m)));

        Collection c = Collections.emptyList();
        f(eval(c));
        f(eval(o(c)));
        
        c = Arrays.asList(new String[]{"x"});
        t(eval(c));
        t(eval(o(c)));
        
        String[] sa = null;
        f(eval(sa));
        f(eval(o(sa)));
        
        sa = new String[]{};
        f(eval(sa));
        f(eval(o(sa)));
        
        sa = new String[]{"x"};
        t(eval(sa));
        t(eval(o(sa)));
    }
    
    @Test
    public void testEscape() {
        String s = null;
        System.out.println(S.escapeCSV(s));
    }
    
    @Test
    public void logTime() {
        t = "@__log_time__";
        s = r(t);
        // TODO how to test this?
    }
    
    @Test
    public void captializeWords() {
        s = S.capitalizeWords("[abc 123 xyz]");
        eq("[Abc 123 Xyz]");
        
        s = S.capitalizeWords("abc@xyz.com");
        eq("Abc@Xyz.Com");
    }
}
