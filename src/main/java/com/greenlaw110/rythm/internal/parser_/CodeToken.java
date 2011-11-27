package com.greenlaw110.rythm.internal.parser_;


public class CodeToken extends Token {
    
    public CodeToken(String s, IContext context) {
        super(s, context);
    }
    
    @Override
    public void output() {
        p(s);
    }
}
