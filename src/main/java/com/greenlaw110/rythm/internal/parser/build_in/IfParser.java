package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;
import com.stevesoft.pat.Regex;

public class IfParser extends BuildInParserFactory {

    @Override
    public IParser create(final DialectBase dialect, final IContext ctx) {
        return new ParserBase(dialect, ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect);
                if (!r.search(remain())) return null;
                
                return new BlockCodeToken(r.stringMatched(2), ctx);
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.IF;
    }
    
    @Override
    protected String patternStr() {
        //return "(%s(%s\\s+\\(.*\\)(\\s*\\{)?)).*";
        return "(%s(%s\\s*\\(" + PatternStr.Expression + "\\)(\\s*\\{)?)).*";
    }
    
    public static void main(String[] args) {
        String p = String.format(new IfParser().patternStr(), "@", "if");
        System.out.println(p);
        
        Regex r = new Regex(p);
        String s = "@if(user.registered()) \n dsfd";
        if (r.search(s)) {
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
    }

}
