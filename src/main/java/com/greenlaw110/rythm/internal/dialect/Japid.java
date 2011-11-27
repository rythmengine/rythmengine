package com.greenlaw110.rythm.internal.dialect;

public class Japid extends Razor {
    
    public static final IDialect INSTANCE = new Japid();

    @Override
    public String a() {
        return "`";
    }

    @Override
    public String e() {
        return "\\$";
    }

    @Override
    public String eo() {
        return "{";
    }

    @Override
    public String ec() {
        return "}";
    }

    @Override
    public String c(){
        return "(\\*{.*?}\\*|`//.*?\\n).*";
    }
    
    @Override
    public String _declare() {
        return "args";
    }

}
