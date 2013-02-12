package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

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
        return new RemoveLeadingLineBreakAndSpacesParser(c) {
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
                return new ExecMacroToken(macro, ctx(), curLine);
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.EXPAND;
    }

    public static void main(String[] args) {
        ExpandParser p = new ExpandParser();
        Regex r = p.reg(Rythm.INSTANCE);
        String s = "@expand(\"x.y.z\") \n@sayHi(\"green\")";
        if (r.search(s)) {
            p(r);
        }
    }
}
