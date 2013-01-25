package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.dialect.BasicRythm;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForEachParser extends KeywordParserFactory {
    private static final ILogger logger = Logger.get(ForEachParser.class);

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = new Regex(String.format(patternStr2(), dialect().a(), keyword()));
                String remain = remain();
                if (!r.search(remain)) {
                    raiseParseException("Error parsing @for statement, correct usage: @for(Type var: Iterable){...} or @for(int i = ...)");
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(2);
                if (s.contains(";")) {
                    if (!ctx().getDialect().enableFreeForLoop()) {
                        throw new TemplateParser.NoFreeLoopException(ctx());
                    }
                    return new BlockCodeToken("for " + s + "{ //line: " + ctx().currentLine() + "\n\t", ctx()) {
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
                    r = reg(dialect());
                    if (!r.search(S.stripBrace(s))) {
                        raiseParseException("Error parsing @for statement, correct usage: @for(Type var: iterable){...}");
                    }
                    String iterable = r.stringMatched(6);
                    String varname = r.stringMatched(5);
                    String type = r.stringMatched(2);
                    if (null != type) type = type.trim();
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
        return "^%s%s\\s*((?@()))\\s*\\{";
    }

    @Override
    protected String patternStr() {
        //return "^(%s%s(\\s*\\(\\s*)(((" + Patterns.Type + "\\s+)?)((" + Patterns.VarName + "))?)\\s*([\\:]?)\\s*(" + Patterns.Expression2 + ")(\\s*\\)?[\\s\\r\\n]*|[\\s\\r\\n]+)\\{?[\\s\\r\\n]*).*";
        //return "^(((" + Patterns.Type + ")\\s+)?(" + Patterns.VarName + ")\\s*\\:\\s*)?(" + Patterns.Expression2 + ")$";
        return "^((([a-zA-Z0-9_\\.]+)(\\s*\\[\\s*\\]|\\s*(?@<>))?\\s+)?(" + Patterns.VarName + ")\\s*\\:\\s*)?(" + Patterns.Expression2 + ")$";
    }

    public static void main(String[] args) {
        Rythm.render("abc\ndds\n@for(dd, dd{}\nadfs");
    }
    private static void test5() {
        List<String> sl = Arrays.asList("a,b,c".split(","));
        String s;
        s = Rythm.render("@for(s:sl){|\n\n|@s, [@s_index], [@s_isOdd], [@s_parity], \n[@s_isFirst], [@s_isLast], [@s_sep], \n[@s_utils.sep(\" and \")]}", sl);
        System.out.println(s);
    }
    
    private static void test4() {
        String campaign = "abc";
        List<String> targets = new ArrayList<String>(Arrays.asList("FACEBOOK,MOBILE".split(",")));
        String s = Rythm.substitute("Campaign launch fee - @campaign on (@for(channels){@(_)@_utils.sep(\" and \")}) channels", campaign, targets);
        System.out.println(s);
    }
    
    private static void test3() {
        Regex r0 = new Regex("");
        ForEachParser p = new ForEachParser();
        Regex r = p.reg(BasicRythm.INSTANCE);
        String s = "@for(play.libs.F.T2<String, String> tab: tabs) {}";
        s = "play.libs.F.T2 [] tab: tabs";
        //s = "String s: sa";
        //s = "sa";
        //s = "@for(int i = 0; i < 5; ++i){:@(i+1) }";
        //s = "x : component.get(\"options\").split(\"-\\\\*\\\\!\\\\*-\")";
        if (r.search(s)) p(r, 8);
        //System.out.println(Rythm.render(s));
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
