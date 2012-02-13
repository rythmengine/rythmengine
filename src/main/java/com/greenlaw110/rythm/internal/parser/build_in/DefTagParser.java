package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @tag tagname(Type var,...) {template...}
 */
public class DefTagParser extends KeywordParserFactory {

    private static class DefTagToken extends BlockToken {
        String tagName;
        String signature;
        public DefTagToken(String tagName, String signature, IContext context) {
            super("", context);
            this.tagName = tagName;
            this.signature = signature;
            ctx.getCodeBuilder().defTag(tagName, signature);
        }

        @Override
        public void openBlock() {
        }

        @Override
        public String closeBlock() {
            ctx.getCodeBuilder().endTag();
            return "";
        }
    }

    @Override
    public Keyword keyword() {
        return Keyword.TAG;
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) return null;
                step(r.stringMatched().length());
                String tagName = r.stringMatched(1);
                String signature = r.stringMatched(2);
                return new DefTagToken(tagName, signature, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^%s%s\\s+([_a-zA-Z][\\w_$]*)\\s*((?@()))\\s*{\\s*\\r*\\n*";
    }
    
    public static void main(String[] args) {
        DefTagParser tp = new DefTagParser();
        Regex r = tp.reg(new Rythm());
        String s = "@tag myTag(String x, User y) {\\n y.name: x\\n}";
        if (r.search(s)) {
            System.out.println("m " + r.stringMatched());
            System.out.println(1 + r.stringMatched(1));
            System.out.println(2 + r.stringMatched(2));
            System.out.println(3 + r.stringMatched(3));
            System.out.println(4 + r.stringMatched(4));
        }
    }

}
