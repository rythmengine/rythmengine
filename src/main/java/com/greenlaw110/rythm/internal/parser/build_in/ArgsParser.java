package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.DialectManager;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.utils.F;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;

public class ArgsParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.ARGS;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            /*
             * parse @args {...}
             */
            public TextBuilder go2(String s) {
                Regex r = reg(dialect());
                final List<F.T4<Integer, String, String, String>> ral = new ArrayList();
                s = s.replaceAll("[\\n\\r]+", ",");
                int line = ctx.currentLine();
                while (r.search(s)) {
                    String type = r.stringMatched(2);
                    checkRestrictedClass(type);
                    String name = r.stringMatched(4);
                    String defVal = r.stringMatched(6);
                    ral.add(new F.T4(line, type, name, defVal));
                }
                return new Directive("", ctx()) {
                    @Override
                    public void call() {
                        for (F.T4<Integer, String, String, String> rd : ral) {
                            builder().addRenderArgs(rd._1, rd._2, rd._3, rd._4);
                        }
                    }
                };
            }

            /*
             * parse @args String s...
             */
            public TextBuilder go() {
                String remain = remain();
                Regex r = new Regex(String.format("%s%s(\\([ \t\f]*\\))?[ \t\f]*((?@{}))", a(), keyword()));
                if (r.search(remain)) {
                    String s = r.stringMatched(2);
                    s = S.strip(s, "{", "}");
                    step(r.stringMatched().length());
                    return go2(s);
                }
                String key = String.format("%s%s ", a(), keyword());
                if (!remain.startsWith(key)) {
                    raiseParseException("No argument declaration found");
                }
                step(key.length());
                remain = remain();
                r = reg(dialect());
                int step = 0;
                //final List<CodeBuilder.RenderArgDeclaration> ral = new ArrayList<CodeBuilder.RenderArgDeclaration>();
                while (r.search(remain)) {
                    String matched = r.stringMatched();
                    if (matched.startsWith("\n") || matched.startsWith("\r")) {
                        break;
                    }
                    step += matched.length();
                    String type = r.stringMatched(2);
                    checkRestrictedClass(type);
                    String name = r.stringMatched(4);
                    String defVal = r.stringMatched(6);
                    //ral.add(new CodeBuilder.RenderArgDeclaration(ctx().currentLine(), name, type, defVal));
                    ctx().getCodeBuilder().addRenderArgs(ctx().currentLine(), type, name, defVal);
                }
                step(step);
                // strip off the following ";" symbol and line breaks
                char c = peek();
                while (true) {
                    c = peek();
                    if ((' ' == c || ';' == c || '\r' == c || '\n' == c) && ctx.hasRemain()) {
                        step(1);
                    } else {
                        break;
                    }
                }
                return new Directive("", ctx()) {
                    @Override
                    public void call() {
//                        for (CodeBuilder.RenderArgDeclaration rd: ral) {
//                            builder().addRenderArgs(rd);
//                        }
                    }
                };
            }
        };
    }

    public static List<CodeBuilder.RenderArgDeclaration> parseArgDeclaration(int lineNo, String s) {
        final List<CodeBuilder.RenderArgDeclaration> ral = new ArrayList<CodeBuilder.RenderArgDeclaration>();
        Regex r = new ArgsParser().reg(DialectManager.current());
        while (r.search(s)) {
            String matched = r.stringMatched();
            if (matched.startsWith("\n") || matched.startsWith("\r")) {
                break;
            }
            String name = r.stringMatched(4);
            String type = r.stringMatched(2);
            String defVal = r.stringMatched(5);
            ral.add(new CodeBuilder.RenderArgDeclaration(lineNo, type, name, defVal));
        }
        return ral;
    }

    public static final String PATTERN = "\\G[ \\t\\x0B\\f]*,?[ \\t\\x0B\\f]*(([\\sa-zA-Z_][\\w$_\\.]*(?@\\<\\>)?(\\[\\])?)[ \\t\\x0B\\f]+([a-zA-Z_][\\w$_]*))([ \\t\\x0B\\f]*=[ \\t\\x0B\\f]*((?@{})|[0-9]+[fLld]?|'[.]'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?";

    public static final String PATTERN2 = "";

    @Override
    protected String patternStr() {
        return PATTERN;
    }

    protected String patternStr0() {
        return "(%s%s([\\s,]+[a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*)+(;|\\r?\\n)+).*";
    }

    public static void main(String[] args) {
        test1();
    }

    public static void test2() {
        Regex r = new Regex("@args(\\([ \t\f]*\\))?[ \t\f]*((?@{}))");
        String s = "@args() {\nString s = 1}";
        p(s, r);
    }

    public static void test1() {
        String s = "@args int x = 99, long y = 100\n@x=@y;\n@{String s = null;\nif (s.length() > 0{\n}\n} \n@{_setOutput(\"c:/t/1.txt\")} \n";
        System.out.println(Rythm.render(s));
    }

}
