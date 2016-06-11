/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.*;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import org.rythmengine.utils.S;

public class ExitIfNoClassParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.EXIT_IF_NO_CLASS;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @debug, correct usage: @__exitIfNoClass__(My.Class.Name)");
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBraceAndQuotation(s);
                try {
                    ctx().getEngine().classLoader().loadClass(s);
                    return new Token("", ctx());
                } catch (Exception e) {
                    throw new TemplateParser.ExitInstruction();
                }
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*((?@()))[\\s]+";
    }
}
