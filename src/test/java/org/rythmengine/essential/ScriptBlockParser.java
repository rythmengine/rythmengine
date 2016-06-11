/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test scripting block parser
 */
public class ScriptBlockParser extends TestBase {
    @Test
    public void test() {
        t = "abc\n@{\n\tint i = 0;\n\tint j = 1;\n}\ni + j = @(i + j)";
        s = r(t);
        assertEquals("abc\ni + j = 1", s);
    }
    
    @Test
    public void testInline() {
        t = "abc@{\n\tint i = 0;\n\tint j = 1;\n}i + j = @(i + j)";
        eq("abci + j = 1");
    }
    
    @Test
    public void testHalfInline() {
        t = "abc@{\n\tint i = 0;\n\tint j = 1;\n}\ni + j = @(i + j)";
        eq("abc\ni + j = 1");
    }

    @Test
    public void testHalfInline2() {
        // this one won't work due to Rythm limit. Fix me!
//        t = "abc\n@{\n\tint i = 0;\n\tint j = 1;\n}i + j = @(i + j)";
//        eq("abc\ni + j = 1");
    }

}
