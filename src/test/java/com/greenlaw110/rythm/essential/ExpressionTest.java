package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.Rythm;
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
            return Rythm.toString(this);
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

        // this will fail, rythm cannot handle that complexity yet.
        // t = "@((d.nullMe() ?: new com.greenlaw110.rythm.essential.ExpressionTest.Data()).now())";
        // s = r(t, from(p("d", d)));
        // eq("foo");
    }

    public static void main(String[] args) {
        run(ExpressionTest.class);
    }

}
