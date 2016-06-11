/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
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
        
        t = "@verbatim(){<pre>abc</pre>}";
        s = r(t);
        eq("<pre>abc</pre>");
    }
    
}
