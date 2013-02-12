package com.greenlaw110.rythm.essential;

import com.greenlaw110.rythm.TestBase;
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
