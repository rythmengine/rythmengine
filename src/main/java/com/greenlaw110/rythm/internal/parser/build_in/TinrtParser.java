package com.greenlaw110.rythm.internal.parser.build_in;

import java.util.regex.Matcher;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.NotRythmTemplateException;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;

/**
 * Parse @tinrt (This Is Not Rythm Template)
 */
public class TinrtParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.TINRT;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());
                if (!m.matches()) return null;
                throw new NotRythmTemplateException();
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s).*";
    }

}
