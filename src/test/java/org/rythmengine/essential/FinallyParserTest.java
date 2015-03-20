package org.rythmengine.essential;

import org.junit.Before;
import org.junit.Test;
import org.rythmengine.TestBase;

/**
 * Test @finally()
 */
public class FinallyParserTest extends TestBase {

    @Before
    public void prepare() {
        System.setProperty("foo", "");
    }

    @Test
    public void finallyShallBeExecutedAfterTemplateGenerated() {
        t = "@finally() {System.setProperty(\"foo\", \"bar\")} hello";
        s = r(t);
        assertEquals(System.getProperty("foo"), "bar");
    }
}
