package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test @verbatim()
 */
public class VerbatimParserTest extends TestBase {
    @Test
    public void test() {
        t = "@verbatim(){@args String s; @s}";
        s = r(t);
        eq("@args String s; @s");

        t = "@verbatim(){\n@args String s\n@s\n}";
        s = r(t);
        eq("@args String s\n@s");
    }
    
}
