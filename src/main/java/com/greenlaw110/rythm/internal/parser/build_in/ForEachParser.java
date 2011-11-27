package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;
import com.stevesoft.pat.Regex;

public class ForEachParser extends BuildInParserFactory {

    @Override
    public IParser create(DialectBase dialect, IContext ctx) {
        return new ParserBase(dialect, ctx) {
            @Override
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a(), keyword()));
                if (!r.search(remain())) return null;
                String s = r.stringMatched(1);
                step(s.length());
                String type = r.stringMatched(3);
                String varname = r.stringMatched(5);
                String iterable = r.stringMatched(6);
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
        return "(%s%s\\s+((" + PatternStr.Type + ")(\\s+(" + PatternStr.VarName + "))?)\\s*\\:\\s*(" + PatternStr.Expression + ")).*";
    }
    
    public static void main(String[] args) {
        String p = String.format(new ForEachParser().patternStr(), "@", "each");
        //String p = "(@each\\s+([a-zA-Z0-9_\\.\\[\\]]+)\\s+([a-zA-Z0-9\\_]+)\\s*\\:\\s*([a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)*)).*"; 
        System.out.println(p);
        
        Regex r = new Regex(p);
        String s = "@each models.User[] u: users.foo()[x] Hello world";
        if (r.search(s)) {
            //System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            //System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
            System.out.println(r.stringMatched(5));
            System.out.println(r.stringMatched(6));
        }
    }

}
