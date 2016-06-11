/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.advanced;

import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;
import org.junit.Before;
import org.junit.Test;

import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_SMART_ESCAPE_ENABLED;
import static org.rythmengine.conf.RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED;

/**
 * Test Smart Escape feature
 */
public class SmartEscapeTest extends TestBase {
    
    @Before
    public void setup() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        System.setProperty(FEATURE_SMART_ESCAPE_ENABLED.getKey(), "true");
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
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
