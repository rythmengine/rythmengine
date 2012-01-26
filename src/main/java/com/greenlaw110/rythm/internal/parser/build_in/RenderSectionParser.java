package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse @render section
 */
public class RenderSectionParser extends KeywordParserFactory {
    @Override
    public Keyword keyword() {
        return Keyword.RENDER_SECTION;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Matcher m = ptn(dialect()).matcher(remain());;
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String section = m.group(4);
                String code = "\n_pSection(\"" + section + "\");\n";
                return new CodeToken(code, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(%s%s\\s*[\\s\\(]\"?'?(" + PatternStr.VarName + ")\"?'?\\)?).*";
    }
    
    public static void main(String[] args) {
        String s = String.format(new RenderSectionParser().patternStr(), "@", Keyword.RENDER_SECTION);
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher("@render abc Hello world");
        if (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(4));
        }
    }

}
