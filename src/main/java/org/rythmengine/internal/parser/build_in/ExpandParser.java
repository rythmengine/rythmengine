/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.dialect.Rythm;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

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
            public Token go() {
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
