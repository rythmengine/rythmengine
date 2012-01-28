package com.greenlaw110.rythm.template;

import java.util.Map;

import com.greenlaw110.rythm.RythmEngine;

public interface ITemplate extends Cloneable {
    /**
     * Set renderArgs in name-value pair
     * @param args
     */
    void setRenderArgs(Map<String, Object> args);
    /**
     * Set renderArgs in position
     * @param args
     */
    void setRenderArgs(Object... args);

    /**
     * Set a render arg by name
     * @param name
     * @param arg
     */
    void setRenderArg(String name, Object arg);
    
    Map<String, Object> getRenderArgs();
    
    Object getRenderArg(String name);

    /**
     * Set a render arg at position
     * @param position
     * @param arg
     */
    void setRenderArg(int position, Object arg);

    /**
     * Render the output
     * @return
     */
    String render();
    
    /**
     * Get a copy of this template instance and pass in the engine and caller
     *
     * @param engine the rythm engine
     * @param out optional, the output buffer
     * @return a cloned instance of this template class
     */
    ITemplate cloneMe(RythmEngine engine, StringBuilder out);
}
