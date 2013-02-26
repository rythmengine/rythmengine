package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test @return
 */
public class ReturnParserTest extends TestBase {
    
    @Test
    public void test() {
        t = "xyz@return abc";
        s = r(t);
        eq("xyz");
    }
    
    @Test
    public void testLineBreaks() {
        t = "xyz\n@return()\nabc";
        s = r(t);
        eq("xyz");
    }
}
