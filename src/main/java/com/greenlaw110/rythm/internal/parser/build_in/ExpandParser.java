package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.List;

/**
 * Parse @expand("myMacro")
 */
public class ExpandParser extends KeywordParserFactory {

    private static final String R = "(^%s(%s\\s*((?@()))\\s*))";

    public ExpandParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new ParserBase(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @expand statement. Correct usage: @expand(\"my-macro\")");
                }
                final int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(3);
                if (S.isEmpty(s)) {
                    raiseParseException("Error parsing @expand statement. Correct usage: @expand(\"my-macro\")");
                }
                final String macro = S.stripBraceAndQuotation(s);
                return new CodeToken(s, ctx()) {
                    int line = curLine;
                    @Override
                    public void output() {
                        CodeBuilder cb = ctx().getCodeBuilder();
                        if (!cb.hasMacro(macro)) {
                            throw new ParseException(ctx().getTemplateClass(), line, "Cannot find macro definition for \"%s\"", macro);
                        }
                        List<TextBuilder> list = cb.getMacro(macro);
                        for (TextBuilder tb: list) {
                            tb.build();
                        }
                    }
                };
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.EXPAND;
    }

    public static void main(String[] args) {
        ExpandParser p = new ExpandParser();
        Regex r = p.reg(new Rythm());
        String s = "@expand(\"x.y.z\") \n@sayHi(\"green\")";
        if (r.search(s)) {
            p(r);
        }
    }
}
