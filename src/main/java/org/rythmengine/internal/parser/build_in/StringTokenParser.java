/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.ParserBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final String PTN = "(%s%s.*?|.*?)([\\n\\r@\\<\\#\\$\\&\\{\\}\\-\\*\\/].*|$)";

    @Override
    public Token go() {
        IContext ctx = ctx();
        String s = ctx.getRemain();
        if (s.length() == 0) {
            return Token.EMPTY_TOKEN;
        }
        String a = a();
        Pattern p = Pattern.compile(String.format(PTN, a, a),
                Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (!m.matches()) {
            return null;
        }
        s = m.group(1);
        if (s.length() == 0) {
            return null;
        }
        ctx.step(s.length());
        s = s.replace(String.format("%s%s", a, a), a).replace("\\", "\\\\");
        if ("".equals(s)) {
            return Token.EMPTY_TOKEN;
        } else {
            return new Token.StringToken(s, ctx);
        }
    }

}
