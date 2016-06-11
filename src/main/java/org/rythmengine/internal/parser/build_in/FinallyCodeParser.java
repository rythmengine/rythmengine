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

/**
 * Parse @finally{arg1 = "";...}
 */
public class FinallyCodeParser extends KeywordParserFactory {

    private static final String R = "(\\n?[ \\t\\x0B\\f]*%s%s\\s*(\\(\\s*\\))?\\s*((?@{}))?[ \\t\\x0B\\f]*\\n?)";

    public FinallyCodeParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    final String matched = r.stringMatched();
                    if (matched.startsWith("\n") || matched.endsWith("\n")) {
                        ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                        Regex r0 = new Regex("\\n([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
                        }
                    } else {
                        Regex r0 = new Regex("([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
                        }
                    }
                    step(matched.length());
                    String s0 = r.stringMatched(3);
                    s0 = s0.substring(1); // strip '{'
                    s0 = s0.substring(0, s0.length() - 1); // strip '}'
                    String s = s0;
                    return new Directive(s, ctx) {
                        @Override
                        public void call() {
                            ctx.getCodeBuilder().setFinalCode(s);
                        }
                    };
                }
                raiseParseException("Invalid @finally syntax found. Correct usage: @init{/*your code come here*/}");
                return null;
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.FINALLY;
    }
}
