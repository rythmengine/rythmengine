/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.template;

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.IEvent;
import org.rythmengine.utils.S;

import java.util.Map;

/**
 * Define a tag
 */
public abstract class TagBase extends TemplateBase implements ITag {

    protected __Body __body;
    
    protected __Body _body; // keep compatibility with previous version
    
    private void setBody(__Body b) {
        __body = b;
        _body = b;
    }

    protected __Body __context;

    private int __line;
    
    protected int __line() {
        return __line;
    }

    private boolean calling;

    /**
     * Check if inside a tag calling context 
     * 
     * <p>Note this is not an API for user application</p>
     * 
     * @return true if is inside a tag calling context
     */
    public boolean __calling() {
        return calling;
    }

    @Override
    public ITemplate __cloneMe(RythmEngine engine, ITemplate caller) {
        TagBase newTag = (TagBase) super.__cloneMe(engine, caller);
        //newTag.setBody(null);
        //newTag.__buffer = new StringBuilder();
        return newTag;
    }

    @Override
    public ITemplate __setRenderArgs(Map<String, Object> args) {
        super.__setRenderArgs(args);
        if (args.containsKey("__body")) setBody((__Body) args.get("__body"));
        return this;
    }

    @Override
    public ITemplate __setRenderArg(String name, Object arg) {
        if ("__body".equals(name)) setBody((__Body) arg);
        super.__setRenderArg(name, arg);
        return this;
    }

    @Override
    public ITag __setBodyContext(__Body body) {
        this.__context = body;
        return this;
    }
    
    protected void __triggerRenderEvent(IEvent<Void, ITemplate> event, RythmEngine engine) {
        if (calling) return; // do not trigger render events while calling as a tag
        event.trigger(engine, this);
    }

    @Override
    public void __call(int line) {
        __line = line;
        calling = true;
        try {
            if (null != __context) {
                __buffer = new StringBuilder();
                __context.p(S.raw(renderWithParent()));
            } else if (null != __caller && null != __buffer) {
                __caller.p(S.raw(renderWithParent())); // a real tag
            } else {
                render(); // an normal template
            }
        } finally {
            calling = false;
        }
    }

    // make sure it does not write to OutputStream or Writer
    private String renderWithParent() {
        if (null != __parent) return render();
        __parent = this;
        try {
            return render();
        } finally {
            __parent = null;
        }
    }

    protected void _pTagBody(__ParameterList parameterList, StringBuilder out) {
        if (null == __body) return;
        __body.render(parameterList, out);
    }

    @Override
    protected void __pLayoutContent() {
        //if (null != __body) __body.render(null, buffer());
        super.__pLayoutContent();
    }

    @Override
    public String __getName() {
        return null;
    }

    public String __str() {
        return String.format("Tag[%s|%s]", this.__getName(), this.getClass());
    }
}
