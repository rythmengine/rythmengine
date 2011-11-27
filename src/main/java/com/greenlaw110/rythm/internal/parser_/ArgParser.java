package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ArgParser extends ParserBase {
    
    private static final String PTN = 
    "(%s%s([\\s,]+[a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*)+(;|\\r?\\n)+).*";

    public ArgParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        Pattern p = Pattern.compile(String.format(PTN, a(), _declare()), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        ctx.step(s.length());
        String declares = s.replaceFirst(String.format("%s%s[\\s]+", a(), _declare()), "");
        return new Directive(declares, ctx) {
            Pattern p = Pattern.compile("[\\s,]*([a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*([\\s]*=([^\\n\\$]+))?)");
            @Override
            public void call() {
                Matcher m = p.matcher(s);
                while (m.find()) {
                    String declare = m.group();
                    declare = declare.replaceFirst("[\\s,]*", "");
                    String[] sa = declare.split("[\\s]+");
                    builder().addRenderArgs(sa[0], sa[1]);
                }
            }
        };
    }
    
    public static void main(String[] args) {
        String sp = String.format(PTN, "@", "var");
        System.out.println(sp);
        Pattern p = Pattern.compile(sp, Pattern.DOTALL);
        Matcher m = p.matcher("@var String name;\nHello World");
        if (m.matches()) {
            System.out.println("matches");
            String s = m.group(1);
            System.out.println(s);
            String declares = s.replaceFirst(String.format("%s%s", "@", "var"), "");
            p = Pattern.compile("[\\s,]+([a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*([\\s]*=([^\\n\\$]+))?)");
            System.out.println(">>>" + declares);
            m = p.matcher(declares);
            while (m.find()) {
                String declare = m.group();
                declare = declare.replaceFirst("[\\s,]*", "");
                System.out.println(declare);
            }
        }
    }

}
