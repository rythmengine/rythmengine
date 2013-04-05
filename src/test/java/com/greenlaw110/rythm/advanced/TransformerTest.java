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
import com.greenlaw110.rythm.extension.Transformer;
import com.greenlaw110.rythm.utils.S;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Transformer
public class TransformerTest extends TestBase {

    public static Integer dbl(Integer i) {
        return i * 2;
    }

    public static String dbl(String s) {
        if (null == s) return "";
        return s + s;
    }

    public static String dbl(Object o) {
        if (null == o) return "";
        return dbl(o.toString());
    }

    @Test
    public void testBuiltInTranformers() {
        System.setProperty("feature.type_inference.enabled", "true");
        Rythm.shutdown();
        String p;

        // raw
        p = "<h1>h1</h1>";
        s = Rythm.render("@1.raw()", p);
        eq(p);

        // escape
        s = Rythm.render("<script>alert('@1.escape()' + x);</script>", "xyz,'abc'");
        eq("<script>alert('xyz,\'abc\'' + x);</script>");

        String[] sa = "json,xml,javascript,html,csv,raw".split(",");
        for (String escape : sa) {
            System.err.println(escape);
            s = Rythm.render(String.format("@1.escape(\"%s\")", escape), p);
            assertEquals(S.escape(p, escape).toString(), s);
        }

        // lowerFirst
        p = "FOO BAR";
        s = Rythm.render("@1.lowerFirst()", p);
        assertEquals("fOO BAR", s);

        // capFirst
        p = "foo bar";
        s = Rythm.render("@1.capFirst()", p);
        assertEquals("Foo bar", s);

        // camelCase
        p = "foo_bar zee";
        s = Rythm.render("@1.camelCase()", p);
        assertEquals("FooBar Zee", s);

        // format date
        Date d = new Date();
        s = Rythm.render("@1.format(\"dd/MM/yyyy\")", d);
        assertEquals(S.format(d, "dd/MM/yyyy"), s);
        s = Rythm.render("@1.format()", d);
        eq(S.format(d));
        
        //format number
        Number n = 113432.33;
        s = r("@1.format()", n);
        eq(S.format(n));
        System.out.println(s);
        n = .03;
        String np = "#,###,###,000.00";
        s = r("@1.format(@2)", n, np);
        eq(S.format(n, np));
        System.out.println(s);
        
        // format currency
        s = Rythm.render("@1.formatCurrency()", 100000/100);
        eq("$1,000.00");
        s = Rythm.render("@args int x;@s().formatCurrency(x)", 100000/100);
        eq("$1,000.00");
        
        // eq
        s = Rythm.render("@1.eq(@2)", "a", "b");
        eq("false");
        
        s = Rythm.render("@1.eq(@2)", "a", "a");
        eq("true");

        s = Rythm.render("@1.eq(@2)", 1, 3);
        eq("false");

        s = Rythm.render("@1.eq(@2)", 1, 1);
        eq("true");

        s = Rythm.render("@1.eq(@2)", null, null);
        eq("true");
    }
    
    @Test
    public void testJoin() {
        System.setProperty("feature.type_inference.enabled", "true");
        Rythm.shutdown();
        List l = Arrays.asList(new Integer[]{1, 2, 3});
        s = r("@1.join()", l);
        eq("1,2,3");
        s = r("@1.join(\";\")", l);
        eq("1;2;3");
        s = r("@1.join(':')", l);
        eq("1:2:3");
    }

    @Test
    public void testUserDefinedTransformer() {
        Rythm.engine().registerTransformer(TransformerTest.class);
        String t = "@args String s, int i\n" +
                "double of \"@s\" is \"@s.app_dbl()\",\n " +
                "double of [@i] is [@i.app_dbl().format(\"0000.00\")]";
        String s = Rythm.render(t, "Java", 99);
        assertContains(s, "double of \"Java\" is \"JavaJava\"");
        assertContains(s, "double of [99] is [0198.00]");
    }

    @Test
    public void testUserDefinedTransformerWithNamespace() {
        // test register with namespace specified
        Rythm.engine().registerTransformer("foo", "", TransformerTest.class);
        String t = "@args String s, int i\n" +
                "double of \"@s\" is \"@s.foo_dbl()\",\n " +
                "double of [@i] is [@i.foo_dbl().format(\"0000.00\")]";
        String s = Rythm.render(t, "Java", 99);
        assertContains(s, "double of \"Java\" is \"JavaJava\"");
        assertContains(s, "double of [99] is [0198.00]");
    }

    public static void main(String[] args) {
        run(TransformerTest.class);
    }
}
