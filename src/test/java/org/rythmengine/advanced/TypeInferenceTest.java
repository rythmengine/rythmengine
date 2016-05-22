/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.advanced;

import org.rythmengine.TestBase;

import static org.rythmengine.conf.RythmConfigurationKey.*;

import org.rythmengine.exception.CompileException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test type inference
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TypeInferenceTest extends TestBase {

    @Before
    public void configure() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
    }

    @Test
    public void test() {
        t = "@1.size() and @2.size()";
        Map m1 = new HashMap();
        m1.put("root/foo", "root/bar");
        Map m2 = new HashMap();
        s = r(t, m1, m2);
        assertEquals("1 and 0", s);
    }
    
    @Test(expected = CompileException.class)
    public void testFeatureDisabled() {
        System.setProperty(FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "false");
        t = "@1.size() and @2.size()";
        Map m1 = new HashMap();
        m1.put("root/foo", "root/bar");
        Map m2 = new HashMap();
        s = r(t, m1, m2);
        assertEquals("1 and 0", s);
    }
    
    @Test()
    public void testCallingWithDifferentType() {
        t = "@1.size() and @2.size()";
        Map m1 = new HashMap();
        m1.put("root/foo", "root/bar");
        Map m2 = new HashMap();
        s = r(t, m1, m2);
        assertEquals("1 and 0", s);

        List l1 = new ArrayList();
        l1.add("xx");
        List l2 = new ArrayList();
        s = r(t, l1, l2);
        assertEquals("1 and 0", s);
    }

    public static void main(String[] args) {
        run(TypeInferenceTest.class);
    }
}
