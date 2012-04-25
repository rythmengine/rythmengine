package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

public class ForEachParser extends KeywordParserFactory {
    private static final ILogger logger = Logger.get(ForEachParser.class);

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                String remain = remain();
                if (!r.search(remain)) {
                    r = new Regex(String.format(patternStr2(), dialect().a(), keyword()));
                    if (!r.search(remain)) {
                        throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing @for statement, correct usage: @for(Type var: iterable){...}");
                    }
                    String s = r.stringMatched(2);
                    step(r.stringMatched().length());
                    return new BlockCodeToken("for " + s + "{\n\t", ctx()) {
                        @Override
                        public void openBlock() {
                            ctx().pushBreak(IContext.Break.BREAK);
                            ctx().pushContinue(IContext.Continue.CONTINUE);
                        }

                        @Override
                        public void output() {
                            super.output();
                        }

                        @Override
                        public String closeBlock() {
                            ctx().popBreak();
                            return super.closeBlock();
                        }
                    };
                } else {
                    String s = r.stringMatched(1);
                    step(s.length());
                    String type = r.stringMatched(5);
                    String varname = r.stringMatched(6);
                    String iterable = r.stringMatched(8);
                    if (S.isEmpty(iterable)) {
                        throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing @for statement, correct usage: @for(Type var: iterable){...}");
                    }
                    return new ForEachCodeToken(type, varname, iterable, ctx());
                }
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.EACH;
    }

    // match for(int i=0; i<100;++i) {
    protected String patternStr2() {
        return "^%s%s\\s*((?@()))\\s*\\{?\\s*";
    }

    @Override
    protected String patternStr() {
        return "^(%s%s(\\s+|\\s*\\(\\s*)((" + Patterns.Type + ")(\\s+(" + Patterns.VarName + "))?)\\s*\\:\\s*(" + Patterns.Expression2 + ")(\\s*\\)?[\\s\\r\\n]*|[\\s\\r\\n]+)\\{?[\\s\\r\\n]*).*";
    }

    public static void main(String[] args) {

        Regex r = new Regex(String.format(new ForEachParser().patternStr2(), "@", "for"));

        String s = "@for(int i = 0; i < 100; ++i){\nHello world}";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(1 + r.stringMatched(1));
            System.out.println(2 + r.stringMatched(2));
            System.out.println(3 + r.stringMatched(3));
            System.out.println(4 + r.stringMatched(4));
            System.out.println(5 + r.stringMatched(5));
            System.out.println(6 + r.stringMatched(6));
            System.out.println(7 + r.stringMatched(7));
            System.out.println(8 + r.stringMatched(8));
        }
    }

}
