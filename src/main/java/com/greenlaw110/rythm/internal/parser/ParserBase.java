package com.greenlaw110.rythm.internal.parser;

import java.util.regex.Pattern;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.dialect.DialectBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;

public abstract class ParserBase implements IParser {
    
    public static final String R_VARNAME = "[a-zA-Z][a-zA-Z0-9_]*";

    public static final Pattern pattern(String regex, Object ... args) {
        if (0 < args.length) regex = String.format(regex, args);
        return Pattern.compile(regex, Pattern.DOTALL);
    }
    
    private final DialectBase d_;
    private final IContext c_;
    
    protected ParserBase(DialectBase dialect, IContext context) {
        if (null == dialect || null == context) throw new NullPointerException();
        d_ = dialect;
        c_ = context;
    }
    
    protected final DialectBase dialect() {
        return d_;
    }
    
    protected final String a() {
        return d_.a();
    }
    
    protected final String remain() {
        return c_.getRemain();
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

}
