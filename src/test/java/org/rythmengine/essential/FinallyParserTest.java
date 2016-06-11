/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
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
