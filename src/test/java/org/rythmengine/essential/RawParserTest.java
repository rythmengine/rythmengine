/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test @raw()
 */
public class RawParserTest extends TestBase {

    /**
     * Test default @raw()
     */
    @Test
    public void testDefault() {
        t = "@raw(){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("<h1>abc</h1>");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@raw(){\n123\n}\nxyz";
        s = r(t);
        eq("abc\n123\nxyz");
    }
    
    @Test
    public void testShortNotation() {
        t = "@raw()@1@";
        s = r(t, "<h1>h1</h1>");
        eq("<h1>h1</h1>");
    }

    public static void main(String[] args) {
        run(RawParserTest.class);
    }
}
