package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Test escape
 */
public class EscapeParserTest extends TestBase {

    /**
     * Test default @escape()
     */
    @Test
    public void testDefault() {
        t = "@escape(){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("&lt;h1&gt;abc&lt;/h1&gt;");
    }
    
    @Test
    public void testParam() {
        t = "@escape(\"html\"){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("&lt;h1&gt;abc&lt;/h1&gt;");

        t = "@escape(\"json\"){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("<h1>abc</h1>");

        t = "@escape(\"json\"){@p}";
        s = r(t, "\"foo\"");
        eq("\\\"foo\\\"");

        t = "@escape(\"javascript\"){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("<h1>abc<\\/h1>");

        t = "@escape(\"javascript\"){@p}";
        s = r(t, "\"foo\"");
        eq("\\\"foo\\\"");

        t = "@escape(\"csv\"){@p}";
        s = r(t, "Someone's good, bad and ...");
        eq("\"Someone's good, bad and ...\"");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@escape(){\n123\n}\nxyz";
        s = r(t);
        eq("abc\n123\nxyz");
    }
    
    @Test
    public void testShortNotation() {
        t = "@escape()@1@";
        s = r(t, "<h1>h1</h1>");
        eq("&lt;h1&gt;h1&lt;/h1&gt;");
    }

    public static void main(String[] args) {
        run(EscapeParserTest.class);
    }
}
