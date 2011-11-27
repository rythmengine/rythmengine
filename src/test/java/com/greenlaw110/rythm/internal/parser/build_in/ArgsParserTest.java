package com.greenlaw110.rythm.internal.parser.build_in;

import org.junit.Test;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.util.TextBuilder;

public class ArgsParserTest extends UnitTest {
    @Test
    public void test() {
        setup("@args String name;");
        IParser p = new ArgsParser().create(d, c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        call(builder);
        assertTrue(b.hasRenderArg("String", "name"));
    }

    @Test
    public void testMultiple() {
        setup("@args String name, int counter;");
        IParser p = new ArgsParser().create(d, c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        call(builder);
        assertTrue(b.hasRenderArg("String", "name"));
        assertTrue(b.hasRenderArg("int", "counter"));
    }
}
