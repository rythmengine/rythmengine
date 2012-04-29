package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.spi.IKeyword;

public enum Keyword implements IKeyword {
    /**
     * Assign enclosed part into a String variable
     */
    ASSIGN,
    /**
     * Declare arguments used in the template
     */
    ARGS,
    /**
     * break current loop
     */
    BREAK,
    /**
     * Cache the block for a certain period
     */
    CACHE,
    /**
     * Continue current loop
     */
    CONTINUE,
    /**
     * output debug message
     */
    DEBUG,
    /**
     * Foreach loop
     */
    EACH("(for|forEach|each)"),
    /**
     * Mark a section that expression should be output after escaped
     */
    ESCAPE,
    /**
     * Exit parsing process if no class loaded in current class loader
     */
    EXIT_IF_NO_CLASS("__exitIfNoClass__"),
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
     * import java packages/classes
     */
    IMPORT,
    /**
     * include other templates
     */
    INCLUDE,
    /**
     * Mark a section init code. There can be
     * at most one @init{} section per template
     */
    INIT,
    /**
     * Explicitly invoke a tag (could be used to implement dynamic tag dispatch
     */
    INVOKE,
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
    RENDER_BODY("(renderBody|doBody)"),
    /**
     * Output sub template section content in place
     */
    RENDER_SECTION("(render(Section|Layout)?|doLayout)"),
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
     * Identify current template is a simple template
     */
    SIMPLE("__simple__"),
    /**
     * output timestamp
     */
    TS,
    /**
     * define tag
     */
    TAG("(tag|def)"),
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
