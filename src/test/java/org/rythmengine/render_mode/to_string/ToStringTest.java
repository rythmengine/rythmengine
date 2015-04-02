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
package org.rythmengine.render_mode.to_string;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.rythmengine.toString.ToStringOption;
import org.rythmengine.toString.ToStringStyle;
import org.junit.Test;

/**
 * Test rythm toString and autoToString
 */
public class ToStringTest extends TestBase {
    
    public ToStringTest() {
    }

    public static class Data {
        private String foo;
        public int bar;
        public transient String tFoo;
        public static int sBar = 0;
        
        public String getFoo() {
            return foo;
        }
    
        public Data(String foo, int bar) {
            this.foo = foo;
            this.bar = bar;
            this.tFoo = foo;
        }
        
        @Override
        public String toString() {
            return Rythm.toString(this);
        }
        
        public String toString(ToStringStyle style) {
            return Rythm.toString(this, null, style);
        }
    }
    
    @Test
    public void testAutoToString() {
        Data data = new Data("bar", 5);
        String s = data.toString();
        assertContains(s, "[foo=bar,bar=5]");
        assertContains(s, "org.rythmengine.advanced.to_string.ToStringTest$Data");
        
        // test with output transient and static fields
        s = Rythm.toString(data, new ToStringOption(true, true), null);
        assertContains(s, "[foo=bar,bar=5,tFoo=bar,sBar=0]");
        
        // test with multi-line style
        s = data.toString(ToStringStyle.MULTI_LINE_STYLE);
        assertMatches(s, ".*foo=bar\\s+bar=5.*");
        
        // test with NO_FIELD_NAMES_STYLE
        s = data.toString(ToStringStyle.NO_FIELD_NAMES_STYLE);
        assertContains(s, "[bar,5]");
        
        // test with SHORT_PREFIX_STYLE
        s = data.toString(ToStringStyle.SHORT_PREFIX_STYLE);
        assertContains(s, "[foo=bar,bar=5]");
        assertContains(s, "ToStringTest.Data");
        assertNotContains(s, "org.rythmengine.advanced.to_string.ToStringTest$Data");
        
        // test with SIMPLE_STYLE
        s = data.toString(ToStringStyle.SIMPLE_STYLE);
        assertEquals(s, "bar,5");
    }
    
    @Test
    public void testToString() {
        Data data = new Data("bar", 5);
        String s = Rythm.toString("@_.getFoo() = @_.bar", data);
        assertEquals("bar = 5", s);
    }

    public static void main(String[] args) {
        run(ToStringTest.class);
    }
}
