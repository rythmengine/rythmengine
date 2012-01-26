package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.PatternStr;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.util.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse tag invocation:
 *
 * @myApp.myTag(
 */
public class InvokeTagParser extends CaretParserFactoryBase {

    public static class InvokeTagToken extends BlockCodeToken {
        String tagName;

        public InvokeTagToken(String s, IContext context) {
            super(s, context);
        }
    }

    @Override
    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr(), dialect().a()));
                if (!r.search(remain())) return null;
                String s = r.stringMatched();
                ctx.step(s.length());
                return null;
            }
        };
    }

    private String patternStr() {
        return "^(%s[a-zA-Z][a-zA-Z$_\\.]+\\s*(?@())*)\\s*";
    }
    
    public static void main(String[] args) {
        String ps = String.format(new InvokeTagParser().patternStr(), "@");
        Regex r = new Regex(ps);
        String s = "@xyz(bar='c', foo=bar.length(), zee=component[foo], \"hello\") < hello hello";
        //String s = "@ is something";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }
    }

}
