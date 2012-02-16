package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.RythmEngine;

import java.util.Map;

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
     * Must be called before real render() happened.
     * Also if the template extends a parent template, then
     * the parent template's init() must be called before this template's init()
     */
    void init();
    
    StringBuilder getOut();
    
    void setOut(StringBuilder sb);
    
    /**
     * Get a copy of this template instance and pass in the engine and caller
     *
     * @param engine the rythm engine
     * @param caller the caller template
     * @return a cloned instance of this template class
     */
    ITemplate cloneMe(RythmEngine engine, ITemplate caller);
}
