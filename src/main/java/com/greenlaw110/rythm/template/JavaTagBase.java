package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.Map;

/**
 * classes extends JavaTagBase are not template based, it's kind of like FastTag in Play
 */
public abstract class JavaTagBase extends TagBase{
    protected  ITag.ParameterList _params;
    protected Body _body;
    public void setRenderArgs(ITag.ParameterList params) {
        _params = params;
        _properties.putAll(params.asMap());
    }
    @Override
    public void setRenderArg(String name, Object val) {
        if ("_body".equals(name)) _body = (Body)val;
        super.setRenderArg(name, val);
    }

    @Override
    public TextBuilder build() {
        call(_params, _body);
        return this;
    }

    /**
     * Subclass overwrite this method and call various p() methods to render the output
     * @param params
     * @param body
     */
    abstract protected void call(ITag.ParameterList params, Body body);
}
