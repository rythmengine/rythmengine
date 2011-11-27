package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IfElseParser extends ParserBase {

    private static final String PTN = "(%s(if(\\(|\\s+).*?\\{|else[\\s\\r\\n]*\\{)|%s[\\s\\r\\n]*\\}[\\s\\r\\n]*else([\\s\\r\\n]|\\s+if(\\(|\\s+).*?)\\{).*";
    
    public IfElseParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        Pattern p = Pattern.compile(String.format(PTN, a(), a()), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        if (s.indexOf('}') > -1) {
            ctx.closeBlock();
        }
        ctx.openBlock(this);
        ctx.step(s.length());
        s = s.replaceFirst(a(), "\n");
        return new CodeToken(s, ctx);
    }
    
    public static void main(String[] args) {
        String s = "@} \nelse if(h > 1) \n {";
        Pattern p = Pattern.compile(String.format(PTN, "@", "@"), Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            s = m.group(1).replaceFirst("@", "\n");
            System.out.println(s);
        }
    }

}
