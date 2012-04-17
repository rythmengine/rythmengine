package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.ICaretParserFactory;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParserFactory;

public abstract class CaretParserFactoryBase implements ICaretParserFactory {

    protected ILogger logger = Logger.get(IParserFactory.class);

    public String getCaret(IDialect dialect) {
        return dialect.a();
    }

    public static void raiseParseException(IContext ctx, String msg, Object... args) {
        throw new ParseException(ctx.getTemplateClass(), ctx.currentLine(), msg, args);
    }
}
