package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @return() statement. Which break the current template execution and return to caller
 */
public class TimestampParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.TS;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @ts, correct usage: @ts()");
                }
                step(r.stringMatched().length());
                return new CodeToken("p(System.currentTimeMillis());", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s\\s*\\(\\s*\\))";
    }

    public static void main(String[] args) {
        String s = "@ts() \naba";
        TimestampParser ap = new TimestampParser();
        Regex r = ap.reg(Rythm.INSTANCE);
        if (r.search(s)) {
            p(r, 5);
        }
    }

}
