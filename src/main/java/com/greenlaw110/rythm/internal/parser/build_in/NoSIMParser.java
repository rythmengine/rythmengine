package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Mark a template as not a SIM template
 */
public class NoSIMParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.NOSIM;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    String s = r.stringMatched();
                    step(s.length());
                }
                return new CodeToken("", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s(?@())?)\\s+";
    }

    public static void main(String[] args) {
        Regex r = new NoSIMParser().reg(new Rythm());
        if (r.search("@nosim() ad")) {
            p(r, 3);
        }
    }

}
