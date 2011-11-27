package com.greenlaw110.rythm.util;

/**
 * This class defines a chained source code builder. It's some how like a StringBuilder but it's chainable
 * 
 * @author luog
 */
public class TextBuilder {

    private final StringBuilder out;
    
    private final TextBuilder caller;
    
    /**
     * Construct a root text builder
     */
    public TextBuilder() {
        out = new StringBuilder();
        caller = null;
    }
    
    /**
     * Construct a chained text builder 
     * @param caller
     */
    public TextBuilder(TextBuilder caller) {
        this.caller = caller;
        out = (null == caller) ? new StringBuilder() : null;
    }
    
    private void p_(Object o) {
        String s = toString(o);
        if (null != out) out.append(s);
        else caller.p(s);
    }
    
    protected String toString(Object o) {
        return null == o ? "" : o.toString();
    }
    
    /**
     * Append the object specified to the string buffer
     * 
     * @param o
     * @return
     */
    public final TextBuilder p(Object o) {
        if (null != o) p_(o);
        return this;
    }
    
    /**
     * Append to object specified to the string buffer and then append
     * an new line character
     *  
     * @param o
     * @return
     */
    protected final TextBuilder pn(Object o) {
        if (null != o) p_(o);
        p_('\n');
        return this;
    }
    
    /**
     * Append an new line character and then append the object specified
     * to the string buffer
     * 
     * @param o
     * @return
     */
    protected final TextBuilder np(Object o) {
        p_('\n');
        if (null != o) p_(o);
        return this;
    }
    
    /**
     * Sub class should implement this method to append the generated
     * source code to the buffer
     * 
     * @return
     */
    public TextBuilder build() {
        return this;
    }
    
    @Override
    public String toString() {
        return null != out ? out.toString() : caller.toString();
    }
}
