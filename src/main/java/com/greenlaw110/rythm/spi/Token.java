package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.utils.TextBuilder;

public class Token extends TextBuilder {
    protected String s;
    protected IContext ctx;
    /*
     * Indicate whether token parse is good
     */
    private boolean ok = true;
    
    protected final void fail() {
        ok = false;
    }
    
    public Token(String s, IContext context) {
        super(null == context ? null : context.getCodeBuilder());
        this.s = s;
        ctx = context;
    }
    
    public boolean test(String line) {
        return true;
    }
    
    public boolean isOk() {
        return ok;
    }
    
    public final TextBuilder build() {
        if (ok) output();
        else {
            pp(s);
        }
        return this;
    }
    
    protected void output() {
        pp(s);
    }
    
    private void pp(String s) {
        s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
        p("\np(\"").p(s).p("\");");
    }
    
}
