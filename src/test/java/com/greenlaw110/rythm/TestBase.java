package com.greenlaw110.rythm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.JUnitCore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Test base class
 */
public abstract class TestBase extends Assert {

    @Before
    public void initRythm() {
        Rythm.shutdown();
    }

    protected void assertNotContains(String found, String notExpected) {
        String msg = String.format("should not contains '%s', found: '%s'", notExpected, found);
        assertTrue(msg, !found.contains(notExpected));
    }

    protected void assertContains(String found, String expected) {
        String msg = String.format("should contains '%s', found: '%s'", expected, found);
        assertTrue(msg, found.contains(expected));
    }
    
    protected void assertMatches(String found, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(found);
        String msg = String.format("should match '%s', found: '%s'", regex, found);
        assertTrue(msg, m.matches());
    }
    
    protected static void run(Class<? extends TestBase> cls) {
        JUnitCore.main(cls.getName());
    }
    
}
