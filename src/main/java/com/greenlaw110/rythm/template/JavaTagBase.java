package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.runtime.ITag;

import java.util.Map;

/**
 * classes extends JavaTagBase are not template based, it's kind of like FastTag in Play
 */
public abstract class JavaTagBase extends TagBase{
    protected  ITag.ParameterList params;
    protected Body _body;
    public void setRenderArgs(ITag.ParameterList params) {
        this.params = params;
    }
    public void setRenderArg(String name, Object val) {
        if ("_body".equals(name)) _body = (Body)val;
    }

    @Override
    public Map<String, Object> getRenderArgs() {
        return params.asMap();
    }
}
