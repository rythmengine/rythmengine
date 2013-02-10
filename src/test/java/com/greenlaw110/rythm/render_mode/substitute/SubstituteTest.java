package com.greenlaw110.rythm.render_mode.substitute;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

import static com.greenlaw110.rythm.utils.NamedParams.*;

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
