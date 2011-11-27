package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The StringToken probe grab plain texts (no special token at all)
 */
public class StringTokenParser extends ParserBase {

    public StringTokenParser(IContext context) {
        super(context);
    }
    
    private static final String PTN = "([^%s%s]+)((%s[^%s]+?|%s[^%s]+?).*|[^%s%s]*$)";

    @Override
    public Token go() {
        String s = ctx.getRemain();
        String a = a();
        String e = e();
        Pattern p = Pattern.compile(String.format(PTN, a, e, a, a, e, e, a, e), Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (!m.matches()) return null;
        s = m.group(1);
        ctx.step(s.length());
        if ("".equals(s.trim())) return new Directive();
        return new Token(s, ctx);
    }
    
    public static void main(String[] args) {
        String s = "Hello \"abc\" ";
        String sp = String.format(PTN, "@", "@", "@", "@", "@", "@", "@", "@");
        String sp1 = "([^@]+)(@[^@]+?.*|[^@]*$)";
        System.out.println(sp);
        Pattern p = Pattern.compile(sp, Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
