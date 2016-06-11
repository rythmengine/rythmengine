/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
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
    public void testIf() {
        t = "xyz@returnIf(1 < 2) abc";
        s = r(t);
        eq("xyz");
        
        t = "xyz @return(1 > 2) abc";
        s = r(t);
        eq("xyz abc");
    }
    
    @Test
    public void testLineBreaks() {
        t = "xyz\n@return()\nabc";
        s = r(t);
        eq("xyz");
    }
}
