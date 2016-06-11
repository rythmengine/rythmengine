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
import org.rythmengine.internal.parser.ParserBase;

/**
 * Parse @get("name")
 */
public class GetParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.GET;
    }

    @Override
    protected String patternStr() {
        return "^(\\n?[ \\t\\x0B\\f]*%s%s((?@())))";
    }

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @get call. Correct usage: @get(\"myVal\")");
                }
                final String matched = r.stringMatched();
                int line = ctx.currentLine();
                if (matched.startsWith("\n") || matched.endsWith("\n")) {
                    if (matched.startsWith("\n")) line = line + 1;
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
                step(r.stringMatched().length()); // remain: @get("name")...
                String s = r.stringMatched(2); // s: ("name")
                s = s.substring(1); // s: "name")
                s = s.substring(0, s.length() - 1); // s: "name"
                r = new Regex("(((?@\"\")|(?@'')|[a-zA-Z_][\\w_]+)(\\s*[:=,]\\s*('.'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?)");

                if (!r.search(s)) {
                    raiseParseException("Error parsing @get tag. Correct usage: @get(\"name\"[:default-value])");
                }
                s = r.stringMatched(2); // propName: "name"
                if (s.startsWith("\"") || s.startsWith("'")) {
                    s = s.substring(1);
                    s = s.substring(0, s.length() - 1);
                    // propName: name
                }
                final String propName = s;
                final String propDefVal = r.stringMatched(4);
                //logger.warn("@get directive is deprecated. Please follow instruction at http://rythmengine.org/doc/directive.md#get to change your code");
                return new Token("", ctx()) {
                    @Override
                    protected void output() {
                        p("\np(__getRenderProperty(\"").p(propName).p("\", ").p(propDefVal == null ? "null" : propDefVal).p("));");
                    }
                };
            }
        };
    }

}
