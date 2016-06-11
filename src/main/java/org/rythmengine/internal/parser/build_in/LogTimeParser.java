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
import org.rythmengine.internal.parser.Directive;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;

public class LogTimeParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.LOG_TIME;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @__logTime__, correct usage: @__logTime__()");
                }
                step(r.stringMatched().length());
                return new Directive("", ctx()) {
                    @Override
                    public void call() {
                        ctx().getCodeBuilder().setLogTime();
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*\\(\\s*\\)[\\r\\n]+";
    }
}
