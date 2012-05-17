package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.DialectNotSupportException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Parse groovy nullable expression, e.g. @foo?.bar()?.zee
 *
 * Must be invoked behind invoking of the normal ExpressionParser
 *
 * @author luog
 */
public class NullableExpressionParser extends CaretParserFactoryBase {

    @Override
    public IParser create(IContext ctx) {

        final IDialect dialect = ctx.getDialect();
        if (!(dialect instanceof Rythm)) throw new DialectNotSupportException(dialect.id());
        final String caret_ = dialect.a();

        return new ParserBase(ctx){

            @Override
            public TextBuilder go() {
                final String caret_ = dialect.a();
                final Regex r1 = new Regex(String.format(patternStr1(), caret_));
                final Regex r2 = new Regex(String.format(patternStr2(), caret_));
                final Regex r3 = new Regex(patternStr3());
                final Regex r4 = new Regex(patternStr4());

                String s = remain();
                String exp = null;
                int step = 0;
                if (r1.search(s)) {
                    exp = r1.stringMatched(2);
                    step = r1.stringMatched().length();
                } else if (r2.search(s)) {
                    exp = r2.stringMatched(2);
                    exp = S.stripBrace(exp);
                    step = r1.stringMatched().length();
                } else {
                    return null;
                }
                exp = exp.trim();
                if (!exp.contains("?")) return null; // leave it to normal expression handler
                if (!r4.search(exp)) {
                    raiseParseException("nullable expression can contain only expression, \"[]\", \"()\", \".\", \"?\", found: %s", exp);
                }
                step(step);
                StringBuilder curExp = new StringBuilder();
                final List<String> statements = new ArrayList<String>();
                while (r3.search(exp)) {
                    String s0 = r3.stringMatched().trim();
                    if (Token.isJavaExtension(s0)) break;
                    if (s0.endsWith("?.")) {
                        s0 = s0.replace("?.", "");
                        curExp.append(s0);
                        String e = curExp.toString();
                        curExp.append(".");
                        statements.add(e);
                    } else if (s0.endsWith(".")) {
                        curExp.append(s0);
                    }
                }
                exp = exp.replaceAll("\\?", "");
                return new CodeToken(exp, ctx()) {
                    @Override
                    public void output() {
                        outputExpression(statements);
                    }
                };
            }
        };
    }

    protected String patternStr1() {
        return "^(%1$s([^\\s\\<\\>%1$s]+))";
    }

    protected String patternStr2() {
        //return "^(%s(@?()))";
        return "^(%s((?@())))";
    }

    protected String patternStr3() {
        return "\\G([a-zA-Z_][\\w]*((?@())(?@[])?|(?@[])(?@())?)?(\\??\\.)?)";
    }

    protected String patternStr4() {
        return "^([a-zA-Z_][\\w]*((?@())(?@[])?|(?@[])(?@())?)?(\\??\\.)?)+$";
    }

    public static void main(String[] args) {
        main2(args);
    }

    public static void main2(String[] args) {
        String s = "abc.foo().format(\"dd/mm/yy\", 5+4)";
        Regex r = new Regex(new NullableExpressionParser().patternStr4());
        System.out.println(r.search(s));
    }

    public static void main1(String[] args) {
        String ps = String.format(new NullableExpressionParser().patternStr1(), "@");
        Regex r = new Regex(ps);
        //s = "@a.b() is something";
        String s = "@foo.bar?.x dd";
        if (r.search(s)) {
            p(r, 5);
        }
        System.out.println();
        ps = String.format(new NullableExpressionParser().patternStr2(), "@");
        r = new Regex(ps);
        s = "@(xyz?.foo()?.bar[]()?.abc)abc";
        if (r.search(s)) {
            p(r, 5);
        }
        System.out.println();
        ps = String.format(new NullableExpressionParser().patternStr3(), "@");
        s = "xyz.foo()?bar[]()?.abc + xyz";
        r = new Regex(ps);
        while (r.search(s)) {
            System.out.println("--------------------------");
            System.out.println(r.stringMatched());
        }
    }

}
