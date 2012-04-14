package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.Token;


public class CodeToken extends Token {
    public CodeToken(String s, IContext context) {
        super(s, context);
    }

    protected CodeToken pt(Object o) {
        p("\t").p(o);
        return this;
    }

    protected CodeToken p2t(Object o) {
        p("\t\t").p(o);
        return this;
    }

    protected CodeToken p3t(Object o) {
        p("\t\t\t").p(o);
        return this;
    }

    protected CodeToken p4t(Object o) {
        p("\t\t\t\t").p(o);
        return this;
    }

    @Override
    public void output() {
        p(s);
        pline();
    }
}
