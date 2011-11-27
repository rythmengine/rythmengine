package com.greenlaw110.rythm.internal.parser_;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.dialect.IDialect;
import com.greenlaw110.rythm.spi.IParser;

public abstract class ParserBase implements IParser, IDialect {

    protected final IContext ctx;

    protected ParserBase(IContext context) {
        if (null == context) throw new NullPointerException();
        ctx = context;
    }
    
    protected final CodeBuilder builder() {
        return ctx.getBuilder();
    }
    
    private IDialect d() {
        return ctx.getDialect();
    }
    
    @Override
    public String a() {
        return d().a();
    }

    @Override
    public String e() {
        return d().e();
    }

    @Override
    public String eo() {
        return d().eo();
    }

    @Override
    public String ec() {
        return d().ec();
    }

    @Override
    public String bo() {
        return d().bo();
    }

    @Override
    public String bc() {
        return d().bc();
    }

    @Override
    public String c(){
        return d().c();
    }
    
    @Override
    public String _import() {
        return d()._import();
    }

    @Override
    public String _declare() {
        return d()._declare();
    }
    
}
