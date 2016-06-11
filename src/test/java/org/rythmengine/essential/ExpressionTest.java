/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import models.JavaBean;
import models.MyPropertyAccessor;
import org.junit.Test;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.IPropertyAccessor;
import org.rythmengine.utils.S;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.rythmengine.conf.RythmConfigurationKey.EXT_PROP_ACCESSOR_IMPLS;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;
import static org.rythmengine.utils.NamedParams.from;
import static org.rythmengine.utils.NamedParams.p;

/**
 * Test Expression output.
 *
 * @see org.rythmengine.advanced.TransformerTest
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
    public void testDynamicExpr() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        System.getProperties().put(EXT_PROP_ACCESSOR_IMPLS.getKey(), new IPropertyAccessor[]{new MyPropertyAccessor()});
        Date today = new Date();
        JavaBean bean = new JavaBean("foo", 11, true, today);
        bean.set("engine", "Rythm");
        t = "@b?.getId()|@b.count@|@b.enabled@|@b.engine@|@b.date.format()@";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("b", bean);
        s = r(t, params);
        contains("foo|11|true|Rythm");
        contains(S.format(today));
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
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.bar()";
        s = r(t, from(p("d", d)));
        eq("");

        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.me()";
        s = r(t, from(p("d", d)));
        eq("");
    }

    @Test
    public void testChainedNullSafe() {
        Data d = new Data();
        d.foo = "bar";
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.me()?.bar()";
        s = r(t, from(p("d", d)));
        eq("barbar");

        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.nullMe()?.bar()";
        s = r(t, from(p("d", d)));
        eq("");

        d = null;
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@d?.me()?.bar()";
        s = r(t, from(p("d", d)));
        eq("");
    }

    @Test
    public void testElvis() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        t = "@args String s\n@(s?:\"foo\")";
        s = r(t, from(p("s", null)));
        eq("foo");

        Data d = new Data();
        t = "@(d.nullMe()?:\"boo\")";
        s = r(t, from(p("d", d)));
        eq("boo");
        
        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@(d.nullMe()?:new org.rythmengine.essential.ExpressionTest.Data())";
        s = r(t, from(p("d", d)));
        eq("nullbar");

        // this will fail, rythm cannot handle that complexity yet.
        // t = "@((d.nullMe() ?: new org.rythmengine.essential.ExpressionTest.Data()).now())";
        // s = r(t, from(p("d", d)));
        // eq("foo");

        // this will also fail, rythm cannot handle mix null safe and elvs:
//        t = "@args org.rythmengine.essential.ExpressionTest.Data d\n@(d?.nullMe()?:new org.rythmengine.essential.ExpressionTest.Data())";
//        s = r(t, from(p("d", d)));
//        eq("nullbar");
    }
    
//    @Test
//    public void testNullSafeExpressionInsideTagParams() {
//        t = "@args models.Bar bar;@bar?._()";
//        s = r(t, null);
//        eq("");
//
//        t = "@args models.Foo foo, models.Bar bar;@foo.bar(bar?._())";
//        s = r(t, from(p("foo", new Foo()), p("bar", new Bar("rab"))));
//        eq("rab");
//        
//        s = r(t, from(p("foo", new Foo()), p("bar", null)));
//        eq("");
//    }
    
    @Test
    public void testEscape() {
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
        t = "@1";
        s = r(t, "<h1>x</h1>");
        eq("&lt;h1&gt;x&lt;/h1&gt;");
    }
    
    public static void main(String[] args) {
        run(ExpressionTest.class);
    }

}
