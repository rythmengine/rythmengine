package com.greenlaw110.rythm.internal.parser;

public enum PatternStr {
    /**
     * Recursive regexp, used only with com.stevesoft.pat.Regex
     */
    Expression("(?@())*"),
    Expression2("[a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.\\=]*)*(?@[])*(?@())*)*"),
    VarName("[_a-zA-Z][a-zA-Z0-9_]*"),
    Blank("([\\s\\r\\n]+)"),
    NewLine("([\r\n]+)"),
    Type("[a-zA-Z0-9_\\.\\[\\]\\<\\>,]+");
    
    private String s_;
    private PatternStr(String pattern) {
        s_ = pattern;
    }
    @Override
    public String toString() {
        return s_;
    }
}
