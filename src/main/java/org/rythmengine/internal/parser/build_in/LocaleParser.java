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
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.S;

import java.util.Locale;

/**
 * Parse @locale("zh", "CN) {...}
 */
public class LocaleParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.LOCALE;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) return null;
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
                String s = r.stringMatched(1);
                s = S.stripBrace(s);
                if (S.isEmpty(s)) {
                    raiseParseException("Error parsing @locale statement. locale parameter expected");
                }
                String code = s;
                Locale locale = (Locale)ctx.getEngine().eval(String.format("org.rythmengine.utils.Eval.locale(%s)", code));
                ctx.pushLocale(locale);
                s = String.format("__ctx.pushLocale(org.rythmengine.utils.Eval.locale(%s));", s);
                
                return new BlockCodeToken(s, ctx()) {
                    @Override
                    public void openBlock() {
                    }

                    @Override
                    public String closeBlock() {
                        ctx.popLocale();
                        return "__ctx.popLocale();";
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^\\n?[ \\t\\x0B\\f]*%s%s\\s*((?@()))[\\s]*\\{?[ \\t\\x0B\\f]*\\n?";
    }

}
