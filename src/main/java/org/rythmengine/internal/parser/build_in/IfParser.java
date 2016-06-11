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
import org.rythmengine.internal.parser.RemoveLeadingSpacesIfLineBreakParser;
import org.rythmengine.utils.S;

public class IfParser extends KeywordParserFactory {

    public static class IfBlockCodeToken extends BlockCodeToken {
        public IfBlockCodeToken(String s, IContext context, int line) {
            super(s, context);
            this.line = line;
        }
    }
    
    @Override
    public IParser create(final IContext ctx) {
        return new RemoveLeadingSpacesIfLineBreakParser(ctx) {
            @Override
            public Token go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @if statement. Correct usage: @if (some-condition) {some-template-code}");
                }
                final String matched = r.stringMatched();
                int line = ctx.currentLine();
                boolean leadingLB = !isLastBuilderLiteral();
                if (matched.startsWith("\n") || matched.endsWith("\n")) {
                    if (matched.startsWith("\n")) {
                        leadingLB = true;
                        line++;
                    }
                    ctx.getCodeBuilder().addBuilder(new Token.StringToken("\n", ctx));
                    if (!matched.startsWith("\n")) {
                        Regex r0 = new Regex("\\n([ \\t\\x0B\\f]*).*");
                        if (r0.search(matched)) {
                            String blank = r0.stringMatched(1);
                            if (blank.length() > 0) {
                                ctx.getCodeBuilder().addBuilder(new Token.StringToken(blank, ctx));
                            }
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
                String s = r.stringMatched(1);
                ctx().step(s.length());
                String sIf = r.stringMatched(3);
                s = r.stringMatched(4);
                s = ExpressionParser.processPositionPlaceHolder(s);
                s = S.stripBrace(s);
                if (s.endsWith("@")) {
                    s = s.substring(0, s.length() - 1);
                    s = "__eval(\"" + s + "\")";
                }
                if ("if".equalsIgnoreCase(sIf)) {
                    s = "\nif (org.rythmengine.utils.Eval.eval(" + s + ")) {";
                } else {
                    s = "\nif (!org.rythmengine.utils.Eval.eval(" + s + ")) {";
                }
                //if (!s.endsWith("{")) s = "\n" + s + " {";
                processFollowingOpenBraceAndLineBreak(leadingLB);
                return new IfBlockCodeToken(s, ctx(), line);
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.IF;
    }

    @Override
    protected String patternStr() {
        //return "(%s(%s\\s+\\(.*\\)(\\s*\\{)?)).*";
        return "(^\\n?[ \\t\\x0B\\f]*%s(%s\\s*((?@()))([ \\t\\x0B\\f]*\\n?))).*";
    }

}
