/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.tag;

import org.rythmengine.TestBase;
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
    public void innerClass() {
        t = "@def class Foo {String bar() {return \"bar\";}}@(new Foo().bar())";
        s = r(t);
        eq("bar");
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

    @Test
    public void testDefClass() {
        t = "@def class Foo {public String foo() {return \"hello foo\";}} @{Foo foo = new Foo()} @foo.foo()";
        s = r(t);
        contains("hello foo");
    }

    @Test
    public void testDefStaticCode() {
        t = "@def static {class Foo {public String foo() {return \"hello foo\";}}} @{Foo foo = new Foo()} @foo.foo()";
        s = r(t);
        contains("hello foo");
    }

    public static void main(String[] args) {
        run(InlineTagTest.class);
    }
    
}
