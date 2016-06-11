/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.render_mode.substitute;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.junit.Test;

import static org.rythmengine.utils.NamedParams.*;

/**
 * Test rythm toString and autoToString
 */
public class SubstituteTest extends TestBase {

    public SubstituteTest() {
    }


    @Test
    public void testGoodTemplate() {
        String t = "Hello @who!";
        String p = "Rythm";
        String s = Rythm.substitute(t, from(p("who", p)));
        assertEquals("Hello Rythm!", s);
    }
    
    @Test(expected = Exception.class)
    public void testBadTemplate() {
        String t = "@(who)'s length is @who.length()";
        String p = "Rythm";
        Rythm.substitute(t, from(p("who", p)));
    }
    
    public static void main(String[] args) {
        run(SubstituteTest.class);
    }
}
