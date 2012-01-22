package com.greenlaw110.rythm.template;

import java.util.Map;

public interface ITemplate {
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
     * Render the output
     * @return
     */
    String render();
}
