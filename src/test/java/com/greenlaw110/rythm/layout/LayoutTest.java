package com.greenlaw110.rythm.layout;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test layout handling
 */
public class LayoutTest extends TestBase {
    @Test
    public void test() {
        s = r("foo/index.html");
        eqf("foo/index.result");
    }
    
    @Test
    public void testSet() {
        s = r("foo/index2.html");
        eqf("foo/index2.result");
    }
    
    @Test
    public void testExtendArgs() {
        s = r("foo/index3.html");
        eqf("foo/index3.result");

        s = r("foo/index4.html");
        eqf("foo/index4.result");
    }
    
    @Test
    public void testRenderSection() {
        s = r("foo/index5.html");
        eqf("foo/index5.result");
    }
}
