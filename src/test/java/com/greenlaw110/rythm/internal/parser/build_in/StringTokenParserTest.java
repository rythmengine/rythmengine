package com.greenlaw110.rythm.internal.parser.build_in;

import org.junit.Test;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.utils.TextBuilder;

public class StringTokenParserTest extends UnitTest {
    
    private void t(String exp, String output) {
        setup(exp);
        IParser p = new StringTokenParser(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        builder.build();
        assertEquals(output, b.toString());
    }
    
    @Test
    public void test() {
        t("Hello world <a href=\"ddd\">xyz</a> @each ...", "\np(\"Hello world <a href=\\\"ddd\\\">xyz</a> \");");
    }

    @Test
    public void testCaretEscape1() {
        t("greenl@@ibm.com", "\np(\"greenl\");");
    }

    @Test
    public void test2testCaretEscape2() {
        t("@@ibm.com", "\np(\"@ibm.com\");");
    }

}
