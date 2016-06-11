/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.tag;

import org.rythmengine.TestBase;
import org.junit.Test;
import org.rythmengine.conf.RythmConfigurationKey;

/**
 * Test invoke other templates
 */
public class InvokeTemplateTest extends TestBase {

    @Test
    public void test() {
        t = "@bar.included()";
        s = r(t);
        eq("included content");
    }
    
    @Test
    public void testWithParameter() {
        t = "@foo.zee.x(\"ss\", 123)";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test
    public void testPassParamByName() {
        t = "@foo.zee.x(y: 123, x = \"ss\")";
        s = r(t);
        eq("x = ss, y = 123");

        t = "@foo.zee.x({y: 123, \n\tx = \"ss\"})";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test
    public void testCurDir() {
        t = "foo/testInvokeTemplateWithCurDir.html";
        eqf("foo/testInvokeTemplateWithCurDir.result");
    }
    
    @Test
    public void testImportPath() {
        t = "@import(){foo.zee.*}\n@x(y: 123, x = \"ss\")";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test
    public void testAssign() {
        t = "@bar.included().assign(x)\n@x";
        s = r(t);
        eq("included content");
    }
    
    @Test
    public void testEscapeResult() {
        t = "@bar.header()";
        s = r(t);
        eq("<h1></h1>");
        
        t = "@bar.header().escape()";
        s = r(t);
        eq("&lt;h1&gt;&lt;/h1&gt;");
    }
    
    @Test
    public void testRenderBody() {
        t = "@bar.doThrice(){Hello}";
        s = r(t);
        eq("Hello\nHello\nHello");
    }
    
    @Test
    public void testRenderBodyCallback() {
        t = "@bar.findEvenElements(Arrays.asList(\"1,2,3,4\".split(\",\"))).callback(String s){\nfound: @s\n}";
        s = r(t);
        // cannot do this as we haven't sorted out redundant empty lines eq("found: 2\nfound: 4");
        assertContains(s, "found: 2");
        assertContains(s, "found: 4");
        assertNotContains(s, "found: 1");
        assertNotContains(s, "found: 3");
    }
    
    @Test
    public void testInvokeWithSuffix() {
        t = "@bar.echo.js(\"rythm\")";
        s = r(t, "rythm");
        eq("alert('rythm')");

        t = "@args String p;@bar.echo(p)";
        s = r(t, "rythm");
        eq("rythm");
    }
    
    @Test
    public void testTagWithCallback() {
        s = r("testTagWithCallback.html", "abc");
        eqf("testTagWithCallback.result");
    }
    
    @Test
    public void testMultipleRoot() {
        System.setProperty(RythmConfigurationKey.HOME_TEMPLATE.getKey(), "root, root2");
        t = "@x.voo()";
        s = r(t);
        eq("voo");
    }

    public static void main(String[] args) {
        run(InvokeTemplateTest.class);
    }
    
}
