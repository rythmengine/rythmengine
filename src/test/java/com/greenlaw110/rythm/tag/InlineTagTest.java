package com.greenlaw110.rythm.tag;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test inline tag define and invocation
 */
public class InlineTagTest extends TestBase {
    @Test
    public void testSimple() {
        t = "@def foo(String s){foo on @s}@foo(\"bar\")";
        s = r(t);
        eq("foo on bar");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@def foo(String s){\nfoo on @s\n}\n@foo(\"bar\")";
        s = r(t);
        eq("abc\nfoo on bar");
    }
    
    @Test
    public void testWithReturnValue() {
        t = "@def int dbl(int i){return i * 2;}@dbl(2)";
        s = r(t);
        eq("4");
    }
    
    @Test
    public void testShortNotation() {
        t = "@def int dbl(int i) return i * 2;@ @dbl(5)";
        s = r(t);
        eq("10");
    }
    
    @Test
    public void testAlias() {
        t = "@tag int dbl(int i) return i * 2;@ @dbl(5)";
        s = r(t);
        eq("10");

        t = "@tag int dbl(int i){return i * 2;}@dbl(2)";
        s = r(t);
        eq("4");

        t = "abc\n@tag foo(String s){\nfoo on @s\n}\n@foo(\"bar\")";
        s = r(t);
        eq("abc\nfoo on bar");
    }

    public static void main(String[] args) {
        run(InlineTagTest.class);
    }
    
}
