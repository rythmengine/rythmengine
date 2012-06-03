package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.ICaretParserFactory;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParserFactory;
import com.stevesoft.pat.Regex;

public abstract class CaretParserFactoryBase implements ICaretParserFactory {

    protected ILogger logger = Logger.get(IParserFactory.class);

    public String getCaret(IDialect dialect) {
        return dialect.a();
    }

    public static void raiseParseException(IContext ctx, String msg, Object... args) {
        throw new ParseException(ctx.getTemplateClass(), ctx.currentLine(), msg, args);
    }

    // -- for testing purpose
    protected static void p(int i, Regex r) {
        if (0 == i) {
            System.out.println(i + ": " + r.stringMatched());
        } else {
            System.out.println(i + ": " + r.stringMatched(i));
        }
    }
    protected static void p(String s, Regex r) {
        if (r.search(s)) p(r);
    }
    protected static void p(String s, Regex r, int max) {
        if (r.search(s)) p(r, max);
    }
    protected static void p(Regex r, int max) {
        for (int i = 0; i < max; ++i) {
            p(i, r);
        }
    }
    protected static void p(Regex r) {
        p(r, 6);
    }
}
