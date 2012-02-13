package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.DialectNotSupportException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Single line expression parser
 *
 * @author luog
 */
public class ExpressionParser extends CaretParserFactoryBase {

    @Override
    public IParser create(IContext ctx) {

        Regex r1_ = null, r2_ = null;
        String caret_ = null;
        final IDialect dialect = ctx.getDialect();
        if (dialect instanceof Rythm) {
            caret_ = dialect.a();
            r1_ = new Regex(String.format(patternStr(), caret_));
            r2_ = new Regex(String.format("^(%s(?@())*).*", caret_));
        }
        final Regex r1 = r1_, r2 = r2_;
        final String caret = caret_;
        if (null == r1 || null == r2) {
            throw new DialectNotSupportException(dialect.id());
        }

        return new ParserBase(ctx){
            
            @Override
            public TextBuilder go() {
                String s = remain();
                if (r1.search(s)) {
                    s = r1.stringMatched(1);
                    if (null != s && !"@".equals(s.trim())) {
                        step(s.length());
                        s = s.replaceFirst(caret, "");
                        return new CodeToken(s, ctx()) {
                            //TODO support java bean spec and JavaExtension
                            @Override
                            public void output() {
                                outputExpression();
                            }
                        };
                    }
                }
                s = remain();
                if (r2.search(s)) {
                    s = r2.stringMatched(1);
                    if (null != s && !"@".equals(s.trim())) {
                        step(s.length());
                        return new CodeToken(s.replaceFirst(caret, ""), ctx()) {
                            //TODO support java bean spec and JavaExtension
                            @Override
                            public void output() {
                                outputExpression();
                            }
                        };
                    }
                }
                return null;
            }
        };
    }

    protected String patternStr() {
        return "^(%s[a-zA-Z_][a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)*)*";
    }

    public static void main(String[] args) {
        String ps = "^(@[a-zA-Z][a-zA-Z$_\\.]+\\s*(?@())*).*";
        Regex r = new Regex(ps);
        String s = "@xyz(bar='c', foo=bar.length(), zee=component[foo], \"hello\");";
        //String s = "@ is something";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }

        ps = String.format(new ExpressionParser().patternStr(), "@");
        System.out.println(ps);
        r = new Regex(ps);
        //s = "@a.b() is something";
        s = "@component.left()[3]()[] + 'c'";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            System.out.println(r.stringMatched(1));
        }
    }

}
