package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.DialectNotSupportException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.util.TextBuilder;
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
            r2_ = new Regex(String.format("(%s\\((.*)\\)).*", caret_));
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
                    s = r1.stringMatched();
                    if (null != s) {
                        step(s.length());
                        s = s.replaceFirst(caret, "");
                        return new Token(s, ctx()) {
                            //TODO support java bean spec
                            @Override
                            protected void output() {
                                p("\np(").p(s).p(");");
                            }
                        };
                    }
                } else if (r2.search(s)) {
                    s = r2.stringMatched();
                    if (null != s) {
                        step(s.length());
                        return new Token(r2.stringMatched(2), ctx()) {
                            //TODO support java bean spec
                            @Override
                            protected void output() {
                                p("\np(").p(s).p(");");
                            }
                        };
                    }
                }
                return null;
            }
        };
    }

    protected String patternStr() {
        return "^%s[a-zA-Z][a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.]*)*(?@[])*(?@())*)*";
    }

}
