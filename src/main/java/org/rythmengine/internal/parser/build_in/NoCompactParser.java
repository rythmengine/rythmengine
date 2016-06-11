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
import org.rythmengine.internal.parser.BlockCodeToken;
import org.rythmengine.internal.parser.RemoveLeadingLineBreakAndSpacesParser;

import java.util.regex.Matcher;

/**
 * Parse @nocompact() {...}
 */
public class NoCompactParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.NOCOMPACT;
    }

    public IParser create(final IContext ctx) {
        return new RemoveLeadingLineBreakAndSpacesParser(ctx) {
            public Token go() {
                Matcher m = ptn(dialect()).matcher(remain());
                if (!m.matches()) return null;
                final String matched = m.group(1);
                step(matched.length());
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
                return new BlockCodeToken("", ctx()) {
                    @Override
                    public void openBlock() {
                        ctx().getCodeBuilder().addBuilder(new PushCompactState(false, ctx));
                    }

                    @Override
                    public String closeBlock() {
                        ctx().getCodeBuilder().addBuilder(new PopCompactState(ctx));
                        return "";
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(\\n?[ \\t\\x0B\\f]*%s%s\\s*\\(\\s*\\)[\\s]*\\{?[ \\t\\x0B\\f]*\\n?).*";
    }

}
