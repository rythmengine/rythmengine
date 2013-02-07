package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @return() statement. Which break the current template execution and return to caller
 */
public class ReturnParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.RETURN;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @return, correct usage: @return()");
                }
                step(r.stringMatched().length());
                return new CodeToken("if (true) {return this;}", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(%s%s\\s*(\\(\\s*\\))?[\\s;]*)";
    }

    public static void main(String[] args) {
        String s = "@return \naba";
        ReturnParser ap = new ReturnParser();
        Regex r = ap.reg(Rythm.INSTANCE);
        p(s, r);
    }

}
