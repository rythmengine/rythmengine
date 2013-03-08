package com.greenlaw110.rythm.issue;

import com.greenlaw110.rythm.TestBase;
import com.greenlaw110.rythm.extension.ILang;
import org.junit.Test;

/**
 * Verify https://github.com/greenlaw110/Rythm/issues/117
 */
public class GH117 extends TestBase {
    @Test
    public void test() {
        System.getProperties().put("default.template_lang.impl", ILang.DefImpl.CSV);
        t = "@for(\"FirstName,LastName,Email\"){@__sep}";
        s = r(t);
        eq("FirstName,LastName,Email");
    }
}
