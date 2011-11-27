package com.greenlaw110.rythm.internal.dialect;

public class Razor implements IDialect {
    
    public static final IDialect INSTANCE = new Razor();

    @Override
    public String a() {
        return "@";
    }

    @Override
    public String e() {
        return "@";
    }

    @Override
    public String eo() {
        return "(";
    }

    @Override
    public String ec() {
        return ")";
    }
    
    @Override
    public String bo() {
        return "{";
    }
    
    @Override
    public String bc() {
        return "}";
    }

    @Override
    public String c() {
        return "(@\\*.*?\\*@|@//.*?\\n).*";
    }

    @Override
    public String _import() {
        return "import";
    }

    @Override
    public String _declare() {
        return "var";
    }

}
