package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse @extends path/to/mylayout.html or @extends path.to.mylayout.html
 */
public class ExtendsParser extends KeywordParserFactory {

    @Override
    public Keyword keyword() {
        return Keyword.EXTENDS;
    }
    
    private static void error(IContext ctx) {
        throw new ParseException(ctx.getTemplateClass(), ctx.currentLine(), "Error parsing extends statement. The correct format is @extends(\"my.parent.template\"[, arg1=val1, val2, ...])");
    }

    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    error(ctx());
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(2);
                if (null == s) {
                    error(ctx());
                }
                r = innerPattern;
                if (!r.search(s)) error(ctx());
                
                // process extend target
                s = r.stringMatched(1);
                if (s.startsWith("\"") || s.startsWith("'")) {
                    s = s.substring(1);
                }
                if (s.endsWith("\"") || s.endsWith("'")) {
                    s = s.substring(0, s.length() - 1);
                }
                final String sExtend = s;
                
                // process extend params
                final InvokeTagParser.ParameterDeclarationList params = new InvokeTagParser.ParameterDeclarationList();
                s = r.stringMatched(2);
                if (!S.isEmpty(s)) {
                    r = argsPattern;
                    while (r.search(s)) {
                        params.addParameterDeclaration(r.stringMatched(4), r.stringMatched(5));
                    }
                }
                final int lineNo = currentLine();
                
                return new Directive(s, ctx()) {
                    @Override
                    public void call() {
                        builder().setExtended(sExtend, params, lineNo);
                    }
                };
            }
        };
    }

    @Override
    protected String patternStr() {
        return "(^%s%s)\\s*((?@())[\\s\\r\\n;]*)";
    }
    
    protected static Regex innerPattern = new Regex("\\((.*?)\\s*(,\\s*(.*))?\\)");
    protected static Regex argsPattern = new Regex("\\G(,\\s*)?((([a-zA-Z_][\\w$_]*)\\s*[=:]\\s*)?('.'|(?@\"\")|[0-9\\.]+[l]?|[a-zA-Z_][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*(\\.[a-zA-Z][a-zA-Z0-9_\\.]*(?@())*(?@[])*(?@())*)*))");
    
    protected String patternStr0() {
        return "(%s%s(\\s*\\((.*)\\)|\\s+([_a-zA-Z\\\\\\\\/][a-zA-Z0-9_\\.\\\\\\\\/]+))[;]?)";
    }
    
    private static void test1() {
        Regex r = new ExtendsParser().reg(new Rythm());
        String line = "@extends(\"_panel.html\", a:5, b=foo.bar(4)[1]);";
        if (!r.search(line)) {
            throw new RuntimeException("1");
        }
        System.out.println(r.stringMatched());
        String s = r.stringMatched(2);
        if (null == s) {
            throw new RuntimeException("2");
        }
        r = innerPattern;
        System.out.println(s);
        if (!r.search(s)) {
            throw new RuntimeException("3");
        }

        // process extend target
        s = r.stringMatched(1);
        if (s.startsWith("\"") || s.startsWith("'")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"") || s.endsWith("'")) {
            s = s.substring(0, s.length() - 1);
        }
        final String sExtend = s;
        System.out.println(sExtend);

        // process extend params
        final InvokeTagParser.ParameterDeclarationList params = new InvokeTagParser.ParameterDeclarationList();
        s = r.stringMatched(2);
        if (!S.isEmpty(s)) {
            s = s.replaceFirst(",\\s*", "");
            r = argsPattern;
            while (r.search(s)) {
                params.addParameterDeclaration(r.stringMatched(4), r.stringMatched(5));
            }
        }
        System.out.println(s);
        System.out.println(params);
    }

    public static void main(String[] args) {
        test1();
    }
    
    public static void test0() {
        Regex r = new ExtendsParser().reg(new Rythm());
        String s = "@extends('ab/cd.foo', 'a': 6, \"b\"=null); acd";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
        System.out.println("--------------------");
        s = r.stringMatched(2);
        r = innerPattern;
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
            System.out.println(r.stringMatched(2));
            System.out.println(r.stringMatched(3));
        }
        System.out.println("-----------------");
        s = r.stringMatched(1);
        if (s.startsWith("\"") || s.startsWith("'")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"") || s.endsWith("'")) {
            s = s.substring(0, s.length() - 1);
        }
        System.out.println(s);

//        //s = "main/rythm.html";
//
//        //Pattern p = Pattern.compile("('([_a-zA-Z][\\w_\\.]*)'|([_a-zA-Z][\\w_\\.]*)|\"([_a-zA-Z][\\w_\\.]*)\")");
//        Pattern p = Pattern.compile("('([_a-zA-Z][\\w_\\.]*)'|([_a-zA-Z][\\w_\\.]*)|\"([_a-zA-Z][\\w_\\.]*)\")");
//        Matcher m = p.matcher(s);
//        if (m.matches()) {
//            System.out.println(m.group(1));
//            System.out.println(m.group(4));
//        }
    }

}
