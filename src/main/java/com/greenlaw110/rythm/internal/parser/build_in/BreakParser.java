package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class BreakParser extends KeywordParserFactory {

    private static final String R = "^(%s%s\\s*(\\(\\s*\\))?[\\s;]*)";

    public BreakParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new ParserBase(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    step(r.stringMatched().length());
                    IContext.Break b = ctx().peekBreak();
                    if (null == b) throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Bad @break statement: No loop context");
                    return new CodeToken(b.getStatement(), ctx());
                }
                throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Bad @break statement found. Correct usage: @break()");
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.BREAK;
    }

    public static void main(String[] args) {
        Regex r = new BreakParser().reg(new Rythm());
        String s = "@break()\n\tdd";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }
    }
}
