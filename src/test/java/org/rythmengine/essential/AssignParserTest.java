/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.junit.Test;
import org.rythmengine.TestBase;

/**
 * Test @assign parser
 */
public class AssignParserTest extends TestBase {
    @Test
    public void test() {
        t = "@assign(x){abc}@x";
        s = r(t);
        eq("abc");
        
        t = "@assign(x)abc@ @x";
        s = r(t);
        eq("abc");
    }
    
    @Test
    public void testLineBreaks() {
        t = "abc\n@assign(x){abc}\n@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\nabc\nxyz");

        t = "abc\n@assign(x){\n\tabc\n\txyz}\n@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\n\tabc\n\txyz\nxyz");

        t = "abc\n@assign(x){\n\tabc\n}\n@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\n\tabc\nxyz");

        t = "abc\n@assign(x){abc}@x\nxyz";
        //System.out.println(t);
        s = r(t);
        eq("abc\nabc\nxyz");
    }
    
    @Test
    public void testShortNotation() {
        t = "@assign(x)abc@\n@x";
        s = r(t);
        eq("abc");
    }

    public static void main(String[] args) {
        run(AssignParserTest.class);
    }
}
