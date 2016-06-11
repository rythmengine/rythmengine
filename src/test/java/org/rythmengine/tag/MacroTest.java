/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.tag;

import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test macro definition and invocation
 */
public class MacroTest extends TestBase {
    @Test
    public void testSimpleExec() {
//        t = "@macro(foo){bar}@exec(foo)";
//        s = r(t);
//        eq("bar");
        t = "@macro(bar){@if(true){x}}@exec(bar)";
        s = r(t);
        eq("x");
    }
    
    @Test
    public void testShortNotation() {
        t = "@macro(foo)zoo@ @exec(foo)";
        s = r(t);
        eq("zoo");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@macro(foo){\nbar\n}\n@exec(foo)";
        s = r(t);
        eq("abc\nbar");
    }
    
    @Test
    public void testInvokeMacro() {
        t = "@macro(foo){bar}@foo()";
        s = r(t);
        eq("bar");
    }
    
    public static void main(String[] args) {
        run(MacroTest.class);
    }
}
