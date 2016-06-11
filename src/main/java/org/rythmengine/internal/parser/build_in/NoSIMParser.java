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

/**
 * Mark a template as not a SIM template
 */
public class NoSIMParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.NOSIM;
    }

    public IParser create(IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    String s = r.stringMatched();
                    step(s.length());
                }
                return new CodeToken("", ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^(\\n?[ \\t\\x0B\\f]*%s%s(?@())?)\\s+";
    }

}
