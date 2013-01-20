package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.parser.BlockCodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.Patterns;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.security.SecureExecutingService;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;

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
                        raiseParseException("Error parsing @for statement, correct usage: @for(Type var: Iterable){...} or @for(int i = ...)");
                    }
                    String s = r.stringMatched(2);
                    step(r.stringMatched().length());
                    return new BlockCodeToken("for " + s + "{\n\t" + SecureExecutingService.INTERRUPT_CODE, ctx()) {
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
                        raiseParseException("Error parsing @for statement, correct usage: @for(Type var: iterable){...}");
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
        test2();
    }

    private static void test2() {
        RythmEngine re = new RythmEngine();
        re.recordJavaSourceOnRuntimeError = true;
        re.recordTemplateSourceOnRuntimeError = true;
        String s = "<a href=\"#\">";
        System.out.println(re.render(s));
    }

    private static void test1() {
        String s = "@args List<String> sa\n@for(String s: sa) @s @ else empty !@  ";
        RythmEngine re = new RythmEngine();
        re.recordJavaSourceOnRuntimeError = true;
        re.recordTemplateSourceOnRuntimeError = true;
        List<String> sa = new ArrayList<String>();
        System.out.println(re.render(s, sa, null));

        sa.add("yy");
        System.out.println(re.render(s, sa, null));

        s = "@args String[] sa\n@for(String s: sa) @s @ else empty! @";
        System.out.println(re.render(s, new String[]{"a"}, "ss"));

        System.out.println(re.render(s, new String[]{}, "ss"));

        s = "@args int[] sa\n@for(int s: sa) @s @ else empty! @";
        System.out.println(re.render(s, new int[]{5}, "ss"));

        System.out.println(re.render(s, new int[]{}, "ss"));

        s = "@args Float[] sa\n@for(Float s: sa) @s @ else empty! @";
        System.out.println(re.render(s, new Float[]{5.0f}, "ss"));

        System.out.println(re.render(s, new Float[]{}, "ss"));

        s = "@args String x\n@cache() { abc }";
        for (int i = 0; i < 3; ++i) {
            System.out.println(re.render(s));
        }
    }

}
