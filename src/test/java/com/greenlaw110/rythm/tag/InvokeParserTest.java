package com.greenlaw110.rythm.tag;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * test @invoke()
 */
public class InvokeParserTest extends TestBase {

    @Test
    public void test() {
        t = "@invoke(\"bar.included\")";
        s = r(t);
        eq("included content");
    }

    @Test
    public void testLineBreak() {
        t = "abc\n\t@invoke(\"bar.included\")\n123";
        s = r(t);
        eq("abc\n\tincluded content\n123");
    }

    @Test
    public void testWithParameter() {
        t = "@invoke(\"foo.zee.x\", \"ss\", 123)";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test
    public void testPassParamByName() {
        t = "@invoke(\"foo.zee.x\", y: 123, x = \"ss\")";
        s = r(t);
        eq("x = ss, y = 123");

        t = "@invoke(\"foo.zee.x\", {y: 123, \n\tx = \"ss\"})";
        s = r(t);
        eq("x = ss, y = 123");
    }
    
    @Test(expected = RuntimeException.class)
    public void testNonExisting() {
        t = "@invoke(\"non.exists\")";
        s = r(t);
        eq("");
    }
    
    @Test
    public void testNonExistingWithIgnore() {
        t = "@invoke(\"non.exists\").ignoreNonExistsTag()";
        s = r(t);
        eq("");
    }

    public static void main(String[] args) {
        run(InvokeParserTest.class);
    }
}
