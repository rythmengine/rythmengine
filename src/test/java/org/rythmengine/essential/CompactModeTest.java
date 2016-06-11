/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.junit.Before;
import org.junit.Test;

import static org.rythmengine.conf.RythmConfigurationKey.CODEGEN_COMPACT_ENABLED;

public class CompactModeTest extends TestBase {
    
    @Before
    public void setup() {
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "true");
    }
    
    @Test
    public void testCompactSpace() {
        t = "<h1>   abc</h1>  x";
        eq("<h1> abc</h1> x");
    }
    
    @Test
    public void testLineBreaks() {
        t = "<h1>\n\nabc\n</h1>";
        eq("<h1>\nabc\n</h1>");
    }
    
    @Test
    public void testBlankAfterLineBreak() {
        t = "<h1> \n</h1>";
        eq("<h1>\n</h1>");
    }
    
    @Test
    public void testLineBreakAfterBlank() {
        t = "<h1>\n </h1>";
        eq("<h1>\n</h1>");
    }
    
    @Test
    public void testCompactDisabled() {
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        t = "<h1>   abc</h1>  x";
        eq("<h1>   abc</h1>  x");

        t = "<h1>\n\nabc\n</h1>";
        s = r(t);
        eq("<h1>\n\nabc\n</h1>");
    }

    @Test
    public void testFile() {
        s = r("foo/compact_test.html", "Rythm");
        eqf("foo/compact_test_compact.result");
        Rythm.shutdown();
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "false");
        s = r("foo/compact_test.html", "Rythm");
        eqf("foo/compact_test_no_compact.result");
    }

    public static void main(String[] args) {
        run(CompactModeTest.class);
    }
}
