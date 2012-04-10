package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.spi.IKeyword;

public enum Keyword implements IKeyword {
    /**
     * Declare arguments used in the template
     */
    ARGS,
    /**
     * break current loop
     */
    BREAK,
    /**
     * template Debug
     */
    DEBUG,
    /**
     * Foreach loop
     */
    EACH("(for|forEach|each)"),
    /**
     * Mark a section that expression should be output after excaped
     */
    ESCAPE,
    /**
     * Exit parsign process if no class loaded in current class loader
     */
    EXIT_IF_NOCLASS("__exitIfNoClass__"),
    /**
     * Declare parent template for this template
     */
    EXTENDS,
    /**
     * Fetch named content from this or sub template
     */
    GET,
    /**
     * plain java if else block
     */
    IF,
    /**
     * introduce import package statement
     */
    IMPORT,
    /**
     * Mark a section init code. There can be
     * at most one @init{} section per template
     */
    INIT,
    /**
     * Instruct that this template needs to log execution time
     */
    LOG_TIME("__logTime__"),
    /**
     * Mark a section that expression should be output as raw data
     */
    RAW,
    /**
     * Output sub template render content in place
     */
    RENDER_BODY("(renderBody|doLayout)"),
    /**
     * Output sub template section content in place
     */
    RENDER_SECTION("(render(Section)?)"),
    /**
     * break the current template execution process and return to caller
     */
    RETURN,
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
