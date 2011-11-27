package com.greenlaw110.rythm.internal.parser_;

import com.greenlaw110.rythm.internal.IToken;
import com.greenlaw110.rythm.util.TextBuilder;

public class Token extends TextBuilder implements IToken {
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
        super(null == context ? null : context.getBuilder());
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
    
    public static void main(String[] args) {
        String s = "ABC \"Hello\"";
        Token t = new Token(s, null);
        System.out.println(t.build().toString());
    }
}
