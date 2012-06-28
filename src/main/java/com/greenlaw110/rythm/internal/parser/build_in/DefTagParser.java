package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @tag [return-type] tagname(Type var,...) {template...}
 */
public class DefTagParser extends KeywordParserFactory {

    private static class DefTagToken extends BlockToken {
        String tagName;
        String signature;
        String retType;
        public DefTagToken(String tagName, String retType, String signature, IContext context) {
            super("", context);
            this.retType = retType;
            this.tagName = tagName;
            this.signature = signature;
            ctx.getCodeBuilder().defTag(tagName, retType, signature);
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
                if (!r.search(remain())) {
                    throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing @def, correct usage: @def tagName([arguments...])");
                }
                step(r.stringMatched().length());
                String retType = r.stringMatched(3);
                String tagName = r.stringMatched(6);
                String signature = r.stringMatched(7);
                return new DefTagToken(tagName, retType, signature, ctx());
            }
        };
    }

    @Override
    protected String patternStr() {
        return "^%s%s\\s+(([_a-zA-Z][\\w_$]*(\\s*((?@<>)|(?@[])))?)\\s+)?([_a-zA-Z][\\w_$]*)\\s*((?@()))\\s*{\\s*\\r*\\n*";
    }

    public static void main(String[] args) {
        DefTagParser tp = new DefTagParser();
        Regex r = tp.reg(new Rythm());
        String s = "@tag Map<String, Map<String, Map<String, Long>>> myTag(String x, Map<String, Map<String, Map<String, Long>>> y) {\\n y.name: x\\n}";
        p(s, r, 9);
    }

}
