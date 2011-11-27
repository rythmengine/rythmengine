package com.greenlaw110.rythm.internal;

public enum Keyword {
    /**
     * Declare arguments used in the template
     */
    ARGS,
    /**
     * Output sub template render content in place
     */
    DO_LAYOUT("doLayout"),
    /**
     * Foreach loop
     */
    EACH,
    /**
     * Declare parent template for this template 
     */
    EXTENDS,
    /**
     * plain java For loop
     */
    FOR,
    /**
     * plain java if else block
     */
    IF,
    /**
     * introduce import package statement
     */
    IMPORT,
    /**
     * Fetch named content from sub template
     */
    GET,
    /**
     * Declare named content to be used in parent template
     */
    SET,
    /**
     * Shortcut for tag invocation (Only used in Japid dialect)
     */
    T,
    /**
     * Invoke tag (Only used in Japid dialect)
     */
    TAG;
    
    private final String s;
    private Keyword() {
        this.s = name().toLowerCase();
    }
    private Keyword(String s) {
        this.s = (null == s) ? name().toLowerCase() : s;
    }
    
    @Override
    public String toString() {
        return s;
    }
}
