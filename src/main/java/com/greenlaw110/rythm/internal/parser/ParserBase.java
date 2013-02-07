package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.Sandbox;
import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.parser.build_in.CaretParserFactoryBase;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IDialect;
import com.greenlaw110.rythm.internal.IParser;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

import java.util.regex.Pattern;

public abstract class ParserBase implements IParser {

    public static final ParserBase NULL_INST = new ParserBase() {
        @Override
        public TextBuilder go() {
            return null;
        }
    };

    protected ILogger logger = Logger.get(IParser.class);

    public static final Pattern pattern(String regex, Object ... args) {
        if (0 < args.length) regex = String.format(regex, args);
        return Pattern.compile(regex, Pattern.DOTALL);
    }

    private final IDialect d_;
    private final IContext c_;

    private ParserBase(){d_= null; c_ = null;}

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
    
    protected final void checkRestrictedClass(String code) {
        if (Rythm.insideSandbox()) {
            String s = Sandbox.hasAccessToRestrictedClasses(ctx().getEngine(), code);
            if (null != s) {
                raiseParseException("Access to restricted class [%s] is blocked in sandbox mode", s);
            }
        }
    }


    // -- for testing purpose
    public static void p(int i, Regex r) {
        if (0 == i) {
            System.out.println(i + ": " + r.stringMatched());
        } else {
            System.out.println(i + ": " + r.stringMatched(i));
        }
    }
    public static void p(String s, Regex r) {
        if (r.search(s)) p(r);
    }
    public static void p(String s, Regex r, int max) {
        if (r.search(s)) p(r, max);
    }
    public static void p(Regex r, int max) {
        for (int i = 0; i < max; ++i) {
            p(i, r);
        }
    }
    public static void p(Regex r) {
        p(r, 6);
    }
}
