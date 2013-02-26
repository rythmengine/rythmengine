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
import org.junit.Test;

import java.util.Date;

import static com.greenlaw110.rythm.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;
import static com.greenlaw110.rythm.utils.NamedParams.from;
import static com.greenlaw110.rythm.utils.NamedParams.p;

/**
 * Test Expression output.
 *
 * @see com.greenlaw110.rythm.advanced.TransformerTest
 */
public class ExpressionTest extends TestBase {

    @Test
    public void testSimple() {
        t = "hello @who";
        s = r(t, from(p("who", "world")));
        eq("hello world");
    }

    public static class Data {
        public String foo;

        public String bar() {
            return foo + "bar";
        }

        public Data me() {
            return this;
        }

        public Data nullMe() {
            return null;
        }

        public Date now() {
            return new Date();
        }

        public Date nullNow() {
            return null;
        }

        public String nullFoo = null;

        @Override
        public String toString() {
            return bar();
        }
    }

    @Test
    public void testProperties() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Data d = new Data();
        d.foo = "bar";
        t = "@d.foo";
        s = r(t, from(p("d", d)));
        eq("bar");
    }

    @Test
    public void testMethod() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Data d = new Data();
        d.foo = "bar";
        t = "@d.bar()";
        s = r(t, from(p("d", d)));
        eq("barbar");
    }

    @Test
    public void testComplex() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Data d = new Data();
        d.foo = "bar";
        t = "@(d.bar() + d.foo)";
        s = r(t, from(p("d", d)));
        eq("barbarbar");
    }

    @Test
    public void testNullSafe() {
        Data d = null;
        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@d?.bar()";
        s = r(t, from(p("d", d)));
        eq("");

        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@d?.me()";
        s = r(t, from(p("d", d)));
        eq("");
    }

    @Test
    public void testChainedNullSafe() {
        Data d = new Data();
        d.foo = "bar";
        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@d?.me()?.bar()";
        s = r(t, from(p("d", d)));
        eq("barbar");

        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@d?.nullMe()?.bar()";
        s = r(t, from(p("d", d)));
        eq("");

        d = null;
        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@d?.me()?.bar()";
        s = r(t, from(p("d", d)));
        eq("");
    }

    @Test
    public void testElvs() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        t = "@args String s\n@(s?:\"foo\")";
        s = r(t, from(p("s", null)));
        eq("foo");

        Data d = new Data();
        t = "@(d.nullMe()?:\"boo\")";
        s = r(t, from(p("d", d)));
        eq("boo");

        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@(d.nullMe()?:new com.greenlaw110.rythm.essential.ExpressionTest.Data())";
        s = r(t, from(p("d", d)));
        eq("nullbar");

        // this will fail, rythm cannot handle that complexity yet.
        // t = "@((d.nullMe() ?: new com.greenlaw110.rythm.essential.ExpressionTest.Data()).now())";
        // s = r(t, from(p("d", d)));
        // eq("foo");

        // this will also fail, rythm cannot handle mix null safe and elvs:
//        t = "@args com.greenlaw110.rythm.essential.ExpressionTest.Data d\n@(d?.nullMe()?:new com.greenlaw110.rythm.essential.ExpressionTest.Data())";
//        s = r(t, from(p("d", d)));
//        eq("nullbar");
    }
    
    @Test
    public void testEscape() {
        t = "@1";
        s = r(t, "<h1>x</h1>");
        eq("&lt;h1&gt;x&lt;/h1&gt;");
    }

    public static void main(String[] args) {
        run(ExpressionTest.class);
    }

}
