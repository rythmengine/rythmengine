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
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

public class BreakParser extends KeywordParserFactory {

    private static final String R = "^(\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))?[\\s;]*)";

    public BreakParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(IContext c) {
        return new RemoveLeadingLineBreakAndSpacesParser(c) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Bad @break statement. Correct usage: @break()");
                }
                String matched = r.stringMatched();
                String condition = r.stringMatched(3);
                if (null != condition) {
                    condition = S.stripBrace(condition);
                }
                step(matched.length());
                IContext.Break b = ctx().peekBreak();
                if (null == b) raiseParseException("Bad @break statement: No loop context");
                if (S.notEmpty(condition)) {
                    return new IfThenToken(condition, "break", ctx());
                } else {
                    return new CodeToken(b.getStatement(), ctx());
                }
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.BREAK;
    }

}
