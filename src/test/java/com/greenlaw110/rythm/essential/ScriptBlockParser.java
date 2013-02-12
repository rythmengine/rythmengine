package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
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
}
