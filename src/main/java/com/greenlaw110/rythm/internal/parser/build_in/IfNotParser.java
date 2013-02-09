package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class IfNotParser extends KeywordParserFactory {

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("Error parsing @ifNot statement. Correct usage: @ifNot (some-condition) {some-template-code}");
                }
                String s = r.stringMatched(1);
                ctx.step(s.length());
                s = "if (!" + r.stringMatched(3) + ") {";

                return new IfParser.IfBlockCodeToken(s, ctx);
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.IFNOT;
    }

    @Override
    protected String patternStr() {
        return "(?i)(^%s(%s\\s*((?@()))(\\s*\\{)?)).*";
    }

}
