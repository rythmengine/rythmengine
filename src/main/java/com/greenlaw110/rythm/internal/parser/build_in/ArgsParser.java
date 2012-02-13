package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgsParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.ARGS;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                String remain = remain();
                String key = String.format("%s%s ", a(), keyword());
                if (!remain.startsWith(key)) return null;
                step(key.length());
                remain = remain();
                Regex r = reg(dialect());
                int step = 0;
                final List<CodeBuilder.RenderArgDeclaration> ral = new ArrayList<CodeBuilder.RenderArgDeclaration>();
                while (r.search(remain)) {
                    step += r.stringMatched().length();
                    String name = r.stringMatched(3);
                    String type = r.stringMatched(2);
                    String defVal = r.stringMatched(5);
                    ral.add(new CodeBuilder.RenderArgDeclaration(name, type, defVal));
                }
                step(step);
                // strip off the following ";" symbol
                char c = peek();
                while ((' ' == c || ';' == c) && ctx.hasRemain()) {
                    c = pop();
                }
                return new Directive("", ctx()) {
                    @Override
                    public void call() {
                        for (CodeBuilder.RenderArgDeclaration rd: ral) {
                            builder().addRenderArgs(rd);
                        }
                    }
                };
            }

            public TextBuilder go1() {
                
                Matcher m = ptn(dialect()).matcher(remain());
                if (!m.matches()) return null;
                String s = m.group(1);
                step(s.length());
                String declares = s.replaceFirst(String.format("%s%s[\\s]+", a(), keyword()), "");
                return new Directive(declares, ctx()) {
                    Pattern p = Pattern.compile("[\\s,]*([a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*([\\s]*=([^\\n\\$]+))?)");
                    @Override
                    public void call() {
                        Matcher m = p.matcher(s);
                        while (m.find()) {
                            String declare = m.group();
                            declare = declare.replaceFirst("[\\s,]*", "");
                            String[] sa = declare.split("[\\s]+");
                            builder().addRenderArgs(sa[0], sa[1]);
                        }
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "\\G\\s*,?\\s*(([\\sa-zA-Z_][\\w$_\\.]*(?@\\<\\>)?)\\s+([a-zA-Z_][\\w$_]*))(\\s*=\\s*([0-9]|'[.]'|(?@\"\")|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))?";
    }
    
    protected String patternStr0() {
        return "(%s%s([\\s,]+[a-zA-Z][a-zA-Z0-9_\\.]*(\\<[a-zA-Z][a-zA-Z0-9_\\.,]*\\>)?[\\s]+[a-zA-Z][a-zA-Z0-9_\\.]*)+(;|\\r?\\n)+).*";
    }

    public static void main(String[] args) {
        String s = "  java.util.List<String> bar, int foo = 2, Map<String,Object> myBag=null;\n\t@bar";
        ArgsParser ap = new ArgsParser();
        Regex r = ap.reg(new Rythm());
        System.out.println(r);
        while (r.search(s)) {
            System.out.println("m: " + r.stringMatched());
            System.out.println("1: " + r.stringMatched(1));
            System.out.println("2: " + r.stringMatched(2));
            System.out.println("3: " + r.stringMatched(3));
            System.out.println("4: " + r.stringMatched(4));
            System.out.println("5: " + r.stringMatched(5));
        }
    }

}
