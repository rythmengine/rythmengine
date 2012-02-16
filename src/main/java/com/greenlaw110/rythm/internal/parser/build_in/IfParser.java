package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
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
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing @if statement. Correct usage: @if (some-condition) {some-template-code}");
                }
                String s = r.stringMatched(1);
                ctx.step(s.length());
                s = r.stringMatched(2);
                if (!s.endsWith("{")) s = "\n" + s + " {";
                return new IfBlockCodeToken(s, ctx);
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
        return "(^%s(%s\\s*((?@()))(\\s*\\n*\\s*\\{)?)).*";
    }
    
    public static void main(String[] args) {
        String p = String.format(new IfParser().patternStr(), "@", "if");
        System.out.println(p);
        
        Regex r = new Regex(p);
        String s = "@if (hello) \n\t<td class=\"minus\">@item.getChange()</td>";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
    }

}
