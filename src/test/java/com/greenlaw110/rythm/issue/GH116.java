package com.greenlaw110.rythm.issue;

import com.greenlaw110.rythm.TestBase;
import org.junit.Test;

/**
 * Verify https://github.com/greenlaw110/Rythm/issues/116
 */
public class GH116 extends TestBase {
    @Test
    public void test() {
        t = "PlayRythm Demo - @get(\"title\")";
        s = r(t);
        eq("PlayRythm Demo - ");
    }
}
