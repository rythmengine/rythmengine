package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test utilities
 */
public class UtilsTest extends TestBase {
    @Test
    public void testTS() {
        long now = System.currentTimeMillis();
        t = "@ts()";
        s = r(t);
        long l = Long.valueOf(s);
        assertTrue(l > now);
        assertTrue((l - now) < 1000);
    }
    
    @Test
    public void logTime() {
        t = "@__log_time__";
        s = r(t);
        // TODO how to test this?
    }
}
