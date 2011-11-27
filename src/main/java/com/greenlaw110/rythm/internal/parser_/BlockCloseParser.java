package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BlockCloseParser extends ParserBase {

    private static final String PTN = "(%s[%s\\s\\n]).*";
    
    public BlockCloseParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        if (ctx.currentBlock() == null) return null;
        Pattern p = Pattern.compile(String.format(PTN, a(), bc()), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        ctx.step(s.length());
        s = s.replaceFirst(a(), "");
        return new Token(s, ctx) {
            @Override
            public void output() {
                p("\n}");
            }
        };
    }
    
    public static void main(String[] args) {
        String s = "@}@else{Bye@} @name\n@}";
        Pattern p = Pattern.compile(String.format(PTN, "@", "}"), Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
