package com.greenlaw110.rythm.tag;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test macro definition and invocation
 */
public class MacroTest extends TestBase {
    @Test
    public void testSimpleExec() {
        t = "@macro(foo){bar}@exec(foo)";
        s = r(t);
        eq("bar");
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
    
    @Test
    public void testExpand() {
        t = "@macro(foo){bar}@expand(foo)";
        s = r(t);
        eq("bar");
    }
}
