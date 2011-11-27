package com.greenlaw110.rythm.internal.parser_;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommentParser extends ParserBase {

    public CommentParser(IContext context) {
        super(context);
    }

    @Override
    public Token go() {
        Pattern p = Pattern.compile(c(), Pattern.DOTALL);
        Matcher m = p.matcher(ctx.getRemain());
        if (!m.matches()) return null;
        String s = m.group(1);
        ctx.step(s.length());
        return new Directive();
    }
    
    public static void main(String[] args) {
        Pattern p = Pattern.compile("(@\\*.*?\\*@|@//.*?\\n).*", Pattern.DOTALL);
        Matcher m = p.matcher("@*this is a comment*@ @for (int i = 0; i < 6; ++i) {@if(i % 2 == 0) {Hello@}@else{Bye@} @name\n@}");
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }

}
