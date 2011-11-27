package com.greenlaw110.rythm.internal.dialect;

public interface IDialect {
    /**
     * Used for template author to change the dialect of the current parser. The change effect as soon as this
     * directive encountered and end when the next dialect directive found
     * 
     * syntax:
     * \@dialect japid|play-groovy|rythm
     */
    public final String DIR_DIALECT = "@dialect";
    
    /**
     * Return the primary marker, e.g. "#" in play-groovy, "@" in rythm and "`" in japid. To escape the 
     * marker repeat the marker twice, e.g. "@@", "##", "``"
     * 
     * The marker start a parsing context:
     * - 
     * 
     * @return
     */
    String a();
    
    /**
     * Return the evaluation marker, e.g. "$" in play-groovy and japid dialect
     * Note it might be same as a(), in razor dialect, both a() and b()
     * returns "@"
     * 
     * The second marker char an expression evaluate
     * 
     * @return
     */
    String e();
    
    /**
     * Return open bracket/brace marker used along with the secondary marker.
     * E.g. "{" in play-groovy and japid dialect
     * 
     * @return
     */
    String eo();
    
    /**
     * Return close bracket/brace marker used along with the secondary marker.
     * E.g. "}" in play-groovy and japid dialect
     * 
     * @return
     */
    String ec();
    
    /**
     * Return block open marker, used along with primary marker.
     * E.g @for (...) {
     * 
     * @return
     */
    String bo();
    
    /**
     * Return block close marker, used along with primary marker
     * 
     * e.g. @}. @} could be suppressed to @
     * 
     * @return
     */
    String bc();
    
    /**
     * Return comment match pattern
     * @return
     */
    String c();
    
    /**
     * Return the "import" directive token
     * @return
     */
    String _import();
    
    /**
     * Return the "declare" directive token. In Japid it should be "args"
     * @return
     */
    String _declare();
}
