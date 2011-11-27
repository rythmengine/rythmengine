package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForParser extends ParserBase {

    private final String PTN = "(%sfor(\\(|\\s+).*?\\{).*";
    
    public ForParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        Pattern p = Pattern.compile(String.format(PTN, a()), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        ctx.openBlock(this);
        ctx.step(s.length());
        s = s.replaceFirst(a(), "\n");
        return new CodeToken(s, ctx);
    }

}
