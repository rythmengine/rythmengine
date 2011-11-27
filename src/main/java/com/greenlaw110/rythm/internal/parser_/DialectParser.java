package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.dialect.IDialect;

public class DialectParser extends ParserBase {
    private static final Pattern P_ = Pattern.compile("(" + IDialect.DIR_DIALECT + "\\s+([a-zA-Z][a-zA-Z0-9\\-_]+)(\\r?\\n)*).*", Pattern.DOTALL);

    public DialectParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        String s = ctx.getRemain();
        Matcher m = P_.matcher(s);
        if (!m.matches()) return null;
        ctx.step(m.group(1).length());
        ctx.setDialect(m.group(2));
        return new Directive(null, ctx);
    }
    
    public static void main(String[] args) {
        String s = "@dialect japid `args String name;\n`for (int i = 0; i < 6; ++i) {`if(i % 2 == 0) {Hello`}`else{Bye`} `name\n`}";
        Matcher m = P_.matcher(s);
        if (!m.matches()) return;
        System.out.println(m.group(1));
        System.out.println(m.group(2));
    }

}
