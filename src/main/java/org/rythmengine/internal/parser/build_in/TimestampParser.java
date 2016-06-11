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
import org.rythmengine.internal.parser.CodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;

/**
 * Parse @return() statement. Which break the current template execution and return to caller
 */
public class TimestampParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.TS;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    return null;
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
