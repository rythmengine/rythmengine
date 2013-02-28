package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test include parser
 */
public class IncludeParserTest extends TestBase {
    @Test
    public void test() {
        s = r("foo/includeTest.html");
        eqf("foo/includeTest.result");
    }
    
    @Test
    public void testIncludeInlineFunction() {
        s = r("foo/includeInlineFunction.html");
        eqf("foo/includeInlineFunction.result");
    }
    
    @Test
    public void testIncludeInLayoutTemplate() {
        s = r("foo/includeInLayoutTemplate.html");
        eqf("foo/includeInLayoutTemplate.result");
    }

    public static void main(String[] args) {
        run(IncludeParserTest.class);
    }
}
