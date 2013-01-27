package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.RythmProperties;
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

    public static void main(String[] args) {
        RythmProperties p = new RythmProperties();
        p.put("rythm.mode", Rythm.Mode.dev);
        Rythm.init(p);
        
        String s = Rythm.render("@ifNot(0 < 1) {all good} else {something bad happened}");
        System.out.println(s);
    }

}
