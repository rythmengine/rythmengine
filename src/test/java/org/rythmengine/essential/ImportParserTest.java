/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
import org.rythmengine.exception.CompileException;
import org.junit.Test;

/**
 * Test @import parser
 */
public class ImportParserTest extends TestBase {
    @Test
    public void testSimple() {
        t = "@import org.rythmengine.*, org.rythmengine.conf.RythmConfigurationKey";
        getSource();
        assertContains(s, "import org.rythmengine.*");
        assertContains(s, "import org.rythmengine.conf.RythmConfigurationKey");
    }

    @Test
    public void testFullFormat() {
        t = "@import(){org.rythmengine.*\norg.rythmengine.conf.RythmConfigurationKey}";
        getSource();
        assertContains(s, "import org.rythmengine.*");
        assertContains(s, "import org.rythmengine.conf.RythmConfigurationKey");

        t = "@import(){org.rythmengine.*;\norg.rythmengine.conf.RythmConfigurationKey}";
        getSource();
        assertContains(s, "import org.rythmengine.*");
        assertContains(s, "import org.rythmengine.conf.RythmConfigurationKey");
    }

    @Test(expected = CompileException.class)
    public void testErrorImport() {
        t = "@import assdsfa";
        r(t);
    }

    @Test
    public void testStatic() {
        t = "@import static org.rythmengine.Rythm.*";
        getSource();
        assertContains(s, "import static org.rythmengine.Rythm.*;");
    }

    @Test(expected = CompileException.class)
    public void testStaticError() {
        t = "@import static org.rythmengine.*";
        getSource();
        assertContains(s, "import static org.rythmengine.Rythm.*;");
    }

    @Test
    public void testStaticFullFormat() {
        t = "@import(){static org.rythmengine.Rythm.*\nstatic org.rythmengine.conf.RythmConfigurationKey.*}";
        getSource();
        assertContains(s, "import static org.rythmengine.Rythm.*;");
        assertContains(s, "import static org.rythmengine.conf.RythmConfigurationKey.*;");
    }
    
    @Test
    public void testLineBreak() {
        t = "abc\n@import java.lang.*\nxyz";
        s = r(t);
        eq("abc\nxyz");
        
        t = "abc\n@import(){java.lang.*}\nxyz";
        s = r(t);
        eq("abc\nxyz");
        
        t = "abc\n\t@import(){java.lang.*}\nxyz";
        s = r(t);
        eq("abc\nxyz");
    }

    public static void main(String[] args) {
        run(ImportParserTest.class);
    }

}
