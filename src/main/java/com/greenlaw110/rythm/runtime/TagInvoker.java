package com.greenlaw110.rythm.runtime;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.util.TextBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 26/01/12
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagInvoker {
    private RythmEngine engine = Rythm.engine;
    public TagInvoker(RythmEngine engine) {
        if (null != engine) this.engine = engine;
    }
    
    public void invoke(String tagName, ITag.ParameterList parameters, TemplateBase caller) {
        invoke(tagName, parameters, new TextBuilder() {
            @Override
            public TextBuilder build() {
                p(engine.classes);
                return this;
            }
        }.build().toString(), caller);
    }
    
    public void invoke(String tagName, ITag.ParameterList parameters, String body, TemplateBase caller) {
        
    } 
}
