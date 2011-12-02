package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.util.TextBuilder;
import org.junit.Test;

/**
 * User: luog
 * Date: 2/12/11
 * Time: 3:29 PM
 */
public class CommentParserTest extends UnitTest {

    private void t(String in, String out) {
        setup(in);
        IParser p = new CommentParser().create(c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        builder.build();
        assertEquals(builder.toString(), out);
    }

    @Test
    public void test() {
        t("@// @if (user.registered()) { <p>hello @user.name()</p> @} \n", "if (user.registered()) {");
    }

    @Test
    public void test2() {
        t("@if (user.registered()) <p>hello @user.name()</p> @", "if (user.registered()) {");
    }

}
