package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @exec("myMacro")
 */
public class ExecParser extends KeywordParserFactory {

    private static final String R = "(^%s(%s\\s*((?@()))\\s*))";

    public ExecParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new ParserBase(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @exec statement. Correct usage: @exec(\"my-macro\")");
                }
                final int curLine = ctx().currentLine();
                step(r.stringMatched().length());
                String s = r.stringMatched(3);
                if (S.isEmpty(s)) {
                    raiseParseException("Error parsing @exec statement. Correct usage: @exec(\"my-macro\")");
                }
                final String macro = S.stripBraceAndQuotation(s);
                return new ExecMacroToken(macro, ctx(), curLine);
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.EXEC;
    }

    public static void main(String[] args) {
        ExecParser p = new ExecParser();
        Regex r = p.reg(Rythm.INSTANCE);
        String s = "@exec(\"x.y.z\") \n@sayHi(\"green\")";
        if (r.search(s)) {
            p(r);
        }
    }
}
