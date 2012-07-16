package com.greenlaw110.rythm.internal.parser;

import com.greenlaw110.rythm.internal.IDirective;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.Token;

public class Directive extends Token implements IDirective {

    public Directive() {
        super(null, (IContext)null);
    }

    public Directive(String s, IContext context) {
        super(s, context);
    }

    protected void output() {}

    public void call() {
    }

}
