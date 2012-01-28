package com.greenlaw110.rythm.runtime;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;

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
        ITemplate tag = engine.tags.get(tagName);
        if (null == tag) {
            tag = engine.getTemplate(tagName);
        } else {
            tag = tag.cloneMe(engine, caller.out());
        }
        if (null == tag) {
            throw new RuntimeException("tag not found: " + tagName);
        }
        int pos = 0;
        for (ITag.Parameter param: parameters) {
            if (param.name != null) {
                tag.setRenderArg(param.name, param.value);
            } else {
                tag.setRenderArg(pos++, param.value);
            }
        }
        tag.render();
    }

    public void invoke(String tagName, ITag.ParameterList parameters, String body, TemplateBase caller) {

    }
}
