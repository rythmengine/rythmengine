package com.greenlaw110.rythm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.JUnitCore;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Test base class
 */
public abstract class TestBase extends Assert {

    protected String t;
    protected String s;
    
    protected String r(String template, Object... args) {
        return Rythm.render(template, args);
    }

    @Before
    public void initRythm() {
        Rythm.shutdown();
        System.setProperty(FEATURE_NATURAL_TEMPLATE_ENABLED.getKey(), "false");
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "false");
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_TRANSFORM_ENABLED.getKey(), "true");
        System.setProperty(CODEGEN_COMPACT_ENABLED.getKey(), "true");
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
