/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.template;

import org.rythmengine.utils.TextBuilder;

/**
 * classes extends JavaTagBase are not template based, it's kind of like FastTag in Play
 */
public abstract class JavaTagBase extends TagBase {
    protected __ParameterList _params;
    //protected __Body _body;

    public TemplateBase __setRenderArgs0(__ParameterList params) {
        _params = null == params ? new __ParameterList() : params;
        __renderArgs.putAll(_params.asMap());
        return this;
    }

    @Override
    public ITemplate __setRenderArg(String name, Object val) {
        if ("__body".equals(name)) _body = (__Body) val;
        super.__setRenderArg(name, val);
        return this;
    }

    @Override
    public TextBuilder build() {
        if (null == _params) _params = new __ParameterList();
        call(_params, _body);
        return this;
    }

    @Override
    protected void __internalBuild() {
        build();
    }

    /**
     * Subclass overwrite this method and call various p() methods to render the output
     *
     * @param params
     * @param body
     */
    abstract protected void call(__ParameterList params, __Body body);
    
    
}
