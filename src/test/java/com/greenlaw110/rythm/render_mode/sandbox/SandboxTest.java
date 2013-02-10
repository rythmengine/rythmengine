package com.greenlaw110.rythm.render_mode.sandbox;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.exception.RythmException;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * Test sandbox feature
 */
public class SandboxTest extends TestBase {

    @Test(expected = RythmException.class)
    public void testSecurityViolation() {
        String t = "@{System.exit(1)}";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RythmException.class)
    public void testSecurityViolation1() {
        String t = "@System.getProperty(\"xxx\")";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RythmException.class)
    public void testSecurityViolation2() {
        String t = "@{System.getProperty(\"xxx\")}";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RythmException.class)
    public void testSecurityViolation3() {
        String t = "@{Runtime r = Runtime.getRuntime();r.exit(1);}";
        Rythm.sandbox().render(t);
    }
    
    @Test(expected = RythmException.class)
    public void testSecurityViolation4() {
        String t = "@{RythmEngine re = new RythmEngine();}";
        Rythm.sandbox().render(t);
    }

    @Test(expected = RuntimeException.class)
    public void testTimeout() {
        String t = "@if (true) {@for(;;){}}";
        Rythm.sandbox().render(t);
    }
    
}
