package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.Directive;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Parse @init{arg1 = "";...}
 */
public class InitCodeParser extends KeywordParserFactory {

    private static final String R = "(%s%s\\s*(\\(\\s*\\))?\\s*((?@{})))";

    public InitCodeParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(final IContext c) {
        return new ParserBase(c) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    step(r.stringMatched().length());
                    String s0 = r.stringMatched(3);
                    s0 = s0.substring(1); // strip '{'
                    s0 = s0.substring(0, s0.length() - 1); // strip '}'
                    String s = s0;
                    return new Directive(s, c) {
                        @Override
                        public void call() {
                            c.getCodeBuilder().setInitCode(s);
                        }
                    };
                }
                raiseParseException("Invalid @init syntax found. Correct usage: @init{/*your code come here*/}");
                return null;
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.INIT;
    }

    public static void main(String[] args) {
        Regex r = new InitCodeParser().reg(Rythm.INSTANCE);
        String s = "@init ( ) {\n\tHello world!\n@each X {abc;} \n} xyz";
        if (r.search(s)) {
            String s0 = r.stringMatched(3);
            s0 = s0.substring(1); // strip '{'
            s0 = s0.substring(0, s0.length() - 1); // strip '}'
            System.out.println(r.stringMatched());
            System.out.println(s0);
        }
    }
}
