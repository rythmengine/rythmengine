package com.greenlaw110.rythm.internal.parser.build_in;

import org.junit.Test;

import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.ut.UnitTest;
import com.greenlaw110.rythm.util.TextBuilder;

public class ForEachParserTest extends UnitTest {
    
    private String expected(String iterable, String type, String varname) {
        String prefix = "_".equals(varname) ? "" : varname;
        return String.format("new com.greenlaw110.rythm.runtime.Each(this).render" + 
                "(%s, new com.greenlaw110.rythm.runtime.Each.Body<%s>(){\n\t"+
                "public void render(final %s %s, final int size, final int %s_index, " + 
                "final boolean %s_isOdd, final String %s_parity, final boolean %s_first, " + 
                "final boolean %s_last) {\n", iterable, type, type, varname, prefix, prefix, prefix, prefix, prefix);
    }
    
    private void t(String in, String iterable, String type, String varname) {
        setup(in);
        IParser p = new ForEachParser().create(d, c);
        TextBuilder builder = p.go();
        assertNotNull(builder);
        builder.build();
        assertEquals(builder.toString(), expected(iterable, type, varname));
    }
    
    @Test
    public void test() {
        t("@each User u: users u.getName() @", "users", "User", "u");
    }
    
    @Test
    public void test2() {
        t("@each User person: foo.getUsers() person.name() @", "foo.getUsers()", "User", "person");
    }
}
