package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.util.TextBuilder;

/**
 * The StringToken probe grab plain texts (no special token at all)
 */
public class StringTokenParser extends ParserBase {

    public StringTokenParser(IContext context) {
        super(context);
    }

    /*
     * ([^@]+((@[^@]+?)
     */
    private static final String PTN = "(%s%s.*?|.*?)(%s.*|$)";

    @Override
    public TextBuilder go() {
        IContext ctx = ctx();
        String s = ctx.getRemain();
        String a = a();
        Pattern p = Pattern.compile(String.format(PTN, a, a, a, a),
                Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (!m.matches())
            return null;
        s = m.group(1);
        ctx.step(s.length());
        if ("".equals(s.trim())) s = " ";
        else s = s.replace(String.format("%s%s", a, a), a);
        return new Token(s, ctx);
    }

    public static void main(String[] args) {
        String s = "@@163.com @{for xb}@";
        String sp = String.format(PTN, "@", "@", "@");
        System.out.println(sp);
        Pattern p = Pattern.compile(sp, Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
