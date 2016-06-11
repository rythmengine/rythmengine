/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
import org.junit.Test;

/**
 * Test literal brace parser
 */
public class BraceParserTest extends TestBase {
    
    @Test
    public void test() {
        t = "\nbody {\n\tcolor: #333333;\n\tline-height: 150%;\n}\nthead {\n\tfont-weight: bold;\n\tbackground-color: #CCCCCC;\n)\n";
        s = r(t);
    }
}
