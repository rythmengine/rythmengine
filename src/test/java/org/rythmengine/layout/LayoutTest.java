/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.layout;

import org.rythmengine.TestBase;
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
    
    @Test
    public void testRenderSectionWithDefaultContent() {
        s = r("foo/index6.html");
        eqf("foo/index6.result");
    }

    @Test
    public void testRenderInherited() {
        s = r("foo/index10.html");
        eqf("foo/index10.result");
    }

    @Test
    public void testExtendsTemplateInCurDir() {
        s = r("foo/index7.html");
        eqf("foo/index7.result");
    }
    
    @Test
    public void testExtendsTemplateInImportPaths() {
        s = r("foo/index8.html");
        eqf("foo/index8.result");
    }
    
    @Test
    public void testInit() {
        s = r("foo/index9.html");
        eqf("foo/index9.result");
    }

    public static void main(String[] args) {
        run(LayoutTest.class);
    }
}
