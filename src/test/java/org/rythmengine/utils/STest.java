package org.rythmengine.utils;

import org.junit.Test;
import org.rythmengine.TestBase;

public class STest extends TestBase {

    @Test
    public void testNl2br() {
        String s = "abc\r\nxyz";
        assertEquals("abc<br/>xyz", S.nl2br(s).data);
        s = "abc\n\r\nxyz";
        assertEquals("abc<br/><br/>xyz", S.nl2br(s).data);
        s = "abc\n\nxyz";
        assertEquals("abc<br/><br/>xyz", S.nl2br(s).data);
    }

}
