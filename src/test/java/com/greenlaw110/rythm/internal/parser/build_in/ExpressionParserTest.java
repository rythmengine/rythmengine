package com.greenlaw110.rythm.internal.parser.build_in;

import org.junit.Test;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.utils.TextBuilder;

public class ExpressionParserTest extends UnitTest {
    
    private void t(String exp, String output) {
        setup(exp);
        IParser p = new ExpressionParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        builder.build();
        assertEquals(output, b.toString());
    }
    
    @Test
    public void test() {
        t("@a.b() is good", "\np(a.b());");
    }

    @Test
    public void test2ndStyle() {
        t("@(a.b() + x) is something", "\np((a.b() + x));");
    }

    @Test
    public void testComplexExpression() {
        t("@a.b()[foo.bar()].x() is good", "p(a.b()[foo.bar()].x());");
    }

}
