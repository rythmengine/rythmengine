package com.greenlaw110.rythm.feature.transformer;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.extension.Transformer;
import com.greenlaw110.rythm.utils.S;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

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
        String p, s;

        // raw
        p = "<h1>h1</h1>";
        s = Rythm.render("@1.raw()", p);
        assertEquals(p, s);

        // escape
        p = "<script>alert(\"xyz,'abc'\" + x);</script>";
        s = Rythm.render("@1.escape()", p);
        assertEquals(S.escape(p).toString(), s);

        String[] sa = "json,xml,javascript,html,csv,raw".split(",");
        for (String escape : sa) {
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

        // format
        Date d = new Date();
        s = Rythm.render("@1.format(\"dd/MM/yyyy\")", d);
        assertEquals(S.format(d, "dd/MM/yyyy"), s);
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
        Rythm.engine().registerTransformer("foo", TransformerTest.class);
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
