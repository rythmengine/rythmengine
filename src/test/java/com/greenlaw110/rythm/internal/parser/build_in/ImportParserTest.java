package com.greenlaw110.rythm.internal.parser.build_in;

import org.junit.Test;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.util.TextBuilder;

public class ImportParserTest extends UnitTest {
    @Test
    public void test() {
        setup("@import a.b.z;");
        IParser p = new ImportParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        call(builder);
        assertTrue(b.hasImport("a.b.z"));
    }

    @Test
    public void testMultiple() {
        setup("@import a.b.z x.y.z.*;");
        IParser p = new ImportParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        call(builder);
        assertTrue(b.hasImport("x.y.z.*"));
        assertTrue(b.hasImport("a.b.z"));
    }

    @Test
    public void testStatic() {
        setup("@import static a.b.z;");
        IParser p = new ImportParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        call(builder);
        assertTrue(b.hasImport("static a.b.z"));
    }

    @Test
    public void testStaticMultiple() {
        setup("@import static a.b.z x.y.*, static ab.c0.* static a.b.Z;");
        IParser p = new ImportParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        call(builder);
        assertTrue(b.hasImport("static a.b.z"));
        assertTrue(b.hasImport("x.y.*"));
        assertTrue(b.hasImport("static ab.c0.*"));
        assertTrue(b.hasImport("static a.b.Z"));
    }
}
