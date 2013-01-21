package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.internal.parser.build_in.CaretParserFactoryBase;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.spi.IParser;

import java.util.logging.LoggingMXBean;
import java.util.regex.Pattern;

public abstract class ParserBase implements IParser {

    protected ILogger logger = Logger.get(IParser.class);

    public static final String R_VARNAME = "[a-zA-Z][a-zA-Z0-9_]*";

    public static final Pattern pattern(String regex, Object ... args) {
        if (0 < args.length) regex = String.format(regex, args);
        return Pattern.compile(regex, Pattern.DOTALL);
    }

    private final IDialect d_;
    private final IContext c_;

    protected ParserBase(IContext context) {
        if (null == context) throw new NullPointerException();
        d_ = context.getDialect();
        c_ = context;
    }

    protected final IDialect dialect() {
        return d_;
    }

    protected final String a() {
        return d_.a();
    }

    protected final String remain() {
        return c_.getRemain();
    }

    protected final int currentLine() {
        return c_.currentLine();
    }

    protected final char peek() {
        return c_.peek();
    }

    protected final char pop() {
        return c_.pop();
    }

    protected final IContext ctx() {
        return c_;
    }

    protected final CodeBuilder builder() {
        return c_.getCodeBuilder();
    }

    protected final void step(int i) {
        c_.step(i);
    }

    protected final void raiseParseException(String msg, Object ... args) {
        CaretParserFactoryBase.raiseParseException(ctx(), msg, args);
    }

}
