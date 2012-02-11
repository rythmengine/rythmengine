package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class ForEachParser extends KeywordParserFactory {

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a(), keyword()));
                if (!r.search(remain())) return null;
                String s = r.stringMatched(1);
                step(s.length());
                String type = r.stringMatched(5);
                String varname = r.stringMatched(6);
                String iterable = r.stringMatched(8);
                return new ForEachCodeToken(type, varname, iterable, ctx());
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.EACH;
    }
    
    @Override
    protected String patternStr() {
        return "(%s%s(\\s+|\\s*\\(\\s*)((" + PatternStr.Type + ")(\\s+(" + PatternStr.VarName + "))?)\\s*\\:\\s*(" + PatternStr.Expression2 + ")(\\s*\\)?[\\s\\r\\n]*|[\\s\\r\\n]+)\\{?[\\s\\r\\n]*).*";
    }
    
    public static void main(String[] args) {
        Regex r = new ForEachParser().reg(new Rythm());

        String s = "@for (models.User[] u: users.foo()[x]) {\nHello world";
        if (r.search(s)) {
            //System.out.println(r.stringMatched());
            System.out.println(1 + r.stringMatched(1));
            System.out.println(2 + r.stringMatched(2));
            System.out.println(3 + r.stringMatched(3));
            System.out.println(4 + r.stringMatched(4));
            System.out.println(5 + r.stringMatched(5));
            System.out.println(6 + r.stringMatched(6));
            System.out.println(7 + r.stringMatched(7));
            System.out.println(8 + r.stringMatched(8));
        }
    }

}
