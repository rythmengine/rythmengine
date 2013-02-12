package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IDialect;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingLineBreakAndSpacesParser;
import com.greenlaw110.rythm.internal.parser.RemoveLeadingSpacesIfLineBreakParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class IfParser extends KeywordParserFactory {

    public static class IfBlockCodeToken extends BlockCodeToken {
        public IfBlockCodeToken(String s, IContext context) {
            super(s, context);
        }
    }
    
    @Override
    public IParser create(final IContext ctx) {
        return new RemoveLeadingSpacesIfLineBreakParser(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @if statement. Correct usage: @if (some-condition) {some-template-code}");
                }
                String s = r.stringMatched(1);
                ctx().step(s.length());
                String sIf = r.stringMatched(3);
                s = r.stringMatched(4);
                s = ExpressionParser.processPositionPlaceHolder(s);
                if ("if".equalsIgnoreCase(sIf)) {
                    s = "\nif (com.greenlaw110.rythm.utils.Eval.eval(" + s + ")) {";
                } else {
                    s = "\nif (!com.greenlaw110.rythm.utils.Eval.eval(" + s + ")) {";
                }
                //if (!s.endsWith("{")) s = "\n" + s + " {";
                return new IfBlockCodeToken(s, ctx());
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
        return "(^%s(%s\\s*((?@()))([ \\t\\x0B\\f]*\\{?[ \\t\\x0B\\f]*\\n?))).*";
    }

}
