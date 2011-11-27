package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForToken extends CodeToken {

    private String varName = null;

    public ForToken(String s, IContext context) {
        super(s, context);
        Matcher m = P.matcher(s);
        if (!m.matches()) {
            throw new RuntimeException(String.format("error parsing for statement [%s]: cannot find iterater var name", s));
        }
        varName = m.group(1);
    }

    private static Pattern P = Pattern.compile("for\\s*\\([a-zA-Z][a-zA-Z0-9_\\.]*\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*:.*\\)\\s*\\{", Pattern.DOTALL);
    
    @Override
    public void output() {
        p("\n{\nint ").p(varName).p("_index = 0;");
        p("\nboolean ").p(varName).p("_isLast = false;");
        p("\nboolean ").p(varName).p("_isFirst = true;");
        p("\nboolean ").p(varName).p("_parity = false;");
        pn(s);
        pn(varName).p("_index++;");
        pn(varName).p("_index++;");
    }
    
    public static void main(String[] args) {
        Matcher m = P.matcher("for (String person: userNames) {");
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }


}
