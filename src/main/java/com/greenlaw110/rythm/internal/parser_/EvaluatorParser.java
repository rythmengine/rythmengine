package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stevesoft.pat.Regex;

public class EvaluatorParser extends ParserBase {

    public EvaluatorParser(IContext context) {
        super(context);
    }

    private static final String PTN = "^%s[a-zA-Z][a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)*";
    
    @Override
    public Token go() {
        String s = ctx.getRemain();
        String e = e();
        Regex r = new Regex(String.format(PTN, e));
        r.search(s);
        s = r.stringMatched();
        if (null != s) {
            ctx.step(s.length());
            s = s.replaceFirst(e, "");
            return new Token(s, ctx) {
                //TODO support java bean spec
                @Override
                protected void output() {
                    p("\np(").p(s).p(");");
                }
            };
        }
        return null;
    }
    
    public static void main(String[] args) {
        test1();
    }
    
    public static void test2() {
        String exp = "@(for)(\\s+|\\(|\\{).*";
        Pattern p = Pattern.compile(exp, Pattern.DOTALL);
        Regex r = new Regex(exp);
        String s1 = "@for (int i = 9; i < 1000; ++i) {";
        String s = "";
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            Matcher m = p.matcher(s1);
            if (m.find()) s = m.group(1);
        }
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(s);
        s = "";
        l = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            if (r.search(s1)) s = r.stringMatched(1);
        }
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(s);
    }
    
    public static void test1() {
        String s = "$name[].xy() sd";
        System.out.println(String.format(PTN, "$"));
        Regex r = new Regex(String.format(PTN, "\\$"));
        r.search(s);
        System.out.println(r.stringMatched().replaceFirst("\\$", ""));
    }

}
