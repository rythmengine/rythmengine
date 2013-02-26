package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test @compact() parser
 */
public class NoCompactParserTest extends TestBase {
    
    @Test
    public void test() {
        t = "<h1> abc   </h1>";
        eq(t);
        
        t = "@noCompact(){<h1> abc   </h1>}";
        s = r(t);
        eq("<h1> abc   </h1>");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc@noCompact(){1  2  3}xyz";
        eq("abc1  2  3xyz");
        
        t = "abc\n@noCompact(){1  2  3}\nxyz";
        s = r(t);
        eq("abc\n1  2  3\nxyz");
        
        t = "abc\n@noCompact(){\n1 2   3\n}\nxyz";
        s = r(t);
        eq("abc\n1 2   3\nxyz");
        
        t = "abc\n  @noCompact(){\n1 2   3\n}\nxyz";
        s = r(t);
        eq("abc\n  1 2   3\nxyz");
    }

    @Test
    public void testShortNotation() {
        t = "@noCompact()1 2   3@";
        s = r(t);
        eq("1 2   3");
    }
    
    @Test
    public void testWithCompact() {
        t = "@noCompact(){@compact(){1 2    3} x  y   z}";
        s = r(t);
        eq("1 2 3 x  y   z");
    }
    
    public static void main(String[] args) {
        run(NoCompactParserTest.class);
    }
    
}
