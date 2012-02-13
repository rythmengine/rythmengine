package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse @renderBody
 */
public class RenderBodyParser extends KeywordParserFactory {
    @Override
    public Keyword keyword() {
        return Keyword.RENDER_BODY;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String code = "_pBody();";
                return new CodeToken(code, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s\\s*(\\(\\))?).*";
    }
    
    public static void main(String[] args) {
        String s = String.format(new RenderBodyParser().patternStr(), "@", Keyword.RENDER_BODY);
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher("@render Hello world");
        if (m.find()) {
            System.out.println(m.group(1));
        }
    }

}
