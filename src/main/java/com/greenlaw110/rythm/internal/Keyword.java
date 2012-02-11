package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.spi.IKeyword;

public enum Keyword implements IKeyword {
    /**
     * Declare arguments used in the template
     */
    ARGS,
    /**
     * Foreach loop
     */
    EACH("(for|forEach|each)"),
    /**
     * Declare parent template for this template 
     */
    EXTENDS,
    /**
     * plain java if else block
     */
    IF,
    /**
     * introduce import package statement
     */
    IMPORT,
    /**
     * Fetch named content from this or sub template
     */
    GET,
    /**
     * Output sub template render content in place
     */
    RENDER_BODY("(renderBody|doLayout)"),
    /**
     * Output sub template section content in place
     */
    RENDER_SECTION("(render(Section)?)"),
    /**
     * Declare a section start
     */
    SECTION,
    /**
     * Declare named content to be used in this or parent template
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
    TINRT,
    /**
     * Verbatim tag mark a block of template source shall
     * not be parsed
     */
    VERBATIM;

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

    @Override
    public boolean isRegexp() {
        return !s.equals(name().toLowerCase());
    }
}
