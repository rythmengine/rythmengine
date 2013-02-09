package com.greenlaw110.rythm.internal.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Patterns {
    /**
     * Recursive regexp, used only with com.stevesoft.pat.Regex
     */
    Expression("(?@())*"),
    Expression2("(?@())?[a-zA-Z0-9_\\.]*((\\.[a-zA-Z][a-zA-Z0-9_\\.\\=]*)*(?@[])*(?@())*)*"),
    VarName("[_a-zA-Z][a-zA-Z0-9_]*"),
    Blank("([\\s\\r\\n]+)"),
    NewLine("([\r\n]+)"),
    RESERVED("(if|else|for|null|class|return|break|continue|go|interface|extend|throw|final|finally|private|public|protected|static|void|enum|package|switch|case|do|until|while)"),
    Type("[a-zA-Z0-9_\\.\\[\\]\\<\\>,]+");

    private String s_;
    private Pattern p_;

    private Patterns(String pattern) {
        s_ = pattern;
    }

    @Override
    public String toString() {
        return s_;
    }

    public Pattern pattern() {
        if (null == p_) {
            p_ = Pattern.compile(s_);
        }
        return p_;
    }

    public Matcher matcher(String s) {
        return pattern().matcher(s);
    }

    public boolean matches(String s) {
        return matcher(s).matches();
    }
}
