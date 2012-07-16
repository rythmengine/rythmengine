package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.TextBuilder;


public class CodeToken extends Token {

    protected CodeToken(String s, TextBuilder caller) {
        super(s, caller);
    }

    public CodeToken(String s, IContext context) {
        super(s, context);
    }

    @Override
    public void output() {
        p(s);
        pline();
    }
}
