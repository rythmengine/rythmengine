package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test include parser
 */
public class IncludeParserTest extends TestBase {
    @Test
    public void test() {
        s = r("root/foo/includeTest.html");
        eqf("root/foo/includeTest.result");
    }
    
    @Test
    public void testIncludeInlineFunction() {
        s = r("root/foo/includeInlineFunction.html");
        eqf("root/foo/includeInlineFunction.result");
    }
    
    @Test
    public void testIncludeInLayoutTemplate() {
        s = r("root/foo/includeInLayoutTemplate.html");
        eqf("root/foo/includeInLayoutTemplate.result");
    }

    public static void main(String[] args) {
        run(IncludeParserTest.class);
    }
}
