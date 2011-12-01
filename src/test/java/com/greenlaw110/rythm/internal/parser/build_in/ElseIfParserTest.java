package com.greenlaw110.rythm.internal.parser.build_in;

import org.junit.Test;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.util.TextBuilder;

public class ElseIfParserTest extends UnitTest {
    
    private void t(String in, String out) {
        setup(in);
        IParser p = new IfParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        builder.build();
        assertEquals(builder.toString(), out);
    }
    
    @Test
    public void test() {
        t("@if (user.registered()) { <p>hello @user.name()</p> @}", "if (user.registered()) {");
    }
    
    @Test
    public void test2() {
        t("@if (user.registered()) <p>hello @user.name()</p> @", "if (user.registered()) {");
    }
    
}
