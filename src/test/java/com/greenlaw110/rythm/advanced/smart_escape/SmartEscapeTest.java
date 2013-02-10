package com.greenlaw110.rythm.advanced.smart_escape;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.TestBase;
import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Smart Escape feature
 */
public class SmartEscapeTest extends TestBase {
    
    @Before
    public void setup() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "true");
    }
    
    @Test
    public void test() {
        String p1 = "<h1>h1</h1>";
        String p2 = "tom's store";
        String t = "@1<script>alert('@2');</script>";
        String s = Rythm.render(t, p1, p2);
        assertEquals("&lt;h1&gt;h1&lt;/h1&gt;<script>alert('tom\\'s store');</script>", s);
    }
    
    @Test
    public void testFeatureDisabled() {
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "false");
        String p1 = "<h1>h1</h1>";
        String p2 = "tom's store";
        String t = "@1<script>alert('@2');</script>";
        String s = Rythm.render(t, p1, p2);
        assertEquals("&lt;h1&gt;h1&lt;/h1&gt;<script>alert('tom&apos;s store');</script>", s);
    }
    
    public static void main(String[] args) {
        run(SmartEscapeTest.class);
    }
}
