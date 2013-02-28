package com.greenlaw110.rythm.tag;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

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
    
    public void testImportPath() {
    }

    public static void main(String[] args) {
        run(InvokeTemplateTest.class);
    }
    
}
