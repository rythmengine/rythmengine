package com.greenlaw110.rythm.internal;

public enum Keyword {
    /**
     * Declare arguments used in the template
     */
    ARGS,
    /**
     * Declare this template is a tag
     */
    DEFTAG,
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
     * Output sub template render content in place
     */
    RENDER_BODY("(renderBody|doLayout)"),
    /**
     * Output sub template section content in place
     */
    RENDER_SECTION("(render(Section)?|get)"),
    /**
     * Declare a section start
     */
    SECTION("(section|set)"),
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
    TAG,
    /**
     * This Is Not Rythm Template tag
     */
    TINRT;

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
    
    public boolean isRegexp() {
        return !s.equals(name().toLowerCase());
    }
}
