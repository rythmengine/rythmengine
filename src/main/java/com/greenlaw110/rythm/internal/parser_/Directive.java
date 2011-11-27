package com.greenlaw110.rythm.internal.parser_;

import com.greenlaw110.rythm.internal.IDirective;

public class Directive extends Token implements IDirective {
    
    public Directive() {
        super(null, null);
    }
    
    public Directive(String s, IContext context) {
        super(s, context);
    }
    
    protected void output() {}

    @Override
    public void call() {
    }

}
