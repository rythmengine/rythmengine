/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.junit.Test;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;

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
        eq("<h1>abc</h1>");
    }
    
    @Test
    public void testDefaultHtml() {
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
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
        eq("<h1>abc<\\/h1>");

        t = "@escape(\"json\"){@p}";
        s = r(t, "\"foo\"");
        eq("\\\"foo\\\"");

        t = "@escape(\"javascript\"){@p}";
        s = r(t, "<h1>abc</h1>");
        eq("<h1>abc<\\/h1>");

        t = "@escape(\"javascript\"){@p}";
        s = r(t, "\"foo\" at \\ xb");
        eq("\\\"foo\\\" at \\\\ xb");

        t = "@escape(\"csv\"){@p}";
        s = r(t, "Someone's, good bad and ...");
        eq("\"Someone's, good bad and ...\"");
        
        t = "@args String p;@{String scheme = \"csv\";}\n@escape(scheme){@p}";
        s = r(t, "Someone's good, \"bad\" and ...");
        eq("\n\"Someone's good, \"\"bad\"\" and ...\"");
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
        eq("<h1>h1</h1>");
    }
    
    @Test
    public void testTransform() {
        System.getProperties().put(RythmConfigurationKey.DEFAULT_CODE_TYPE_IMPL.getKey(), ICodeType.DefImpl.HTML);
        t = "@args String foo;@foo.escape()";
        s = r(t, "<h1>abc</h1>");
        eq("&lt;h1&gt;abc&lt;/h1&gt;");
    }
    
    public static void main(String[] args) {
        run(EscapeParserTest.class);
        
//        String s = "abc \\ dfd 'xx' and \"yyy\" \\ dd";
//        System.out.println(Rythm.render("@args String s\n{\"abc\": \"@s.escapeJSON()\"}", s));
    }
}
