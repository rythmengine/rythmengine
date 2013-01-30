package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 25/01/12
 * Time: 12:16 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TagBase extends TemplateBase implements ITag {
    protected ILogger logger = Logger.get(TagBase.class);

    protected Body _body;

    protected Body _context;

    private int _line;    

    protected int _line() {
        return _line;
    }

    @Override
    public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
        Map<String, String> m = null;
        TagBase newTag = (TagBase)super.cloneMe(engine, caller);
        newTag._body = null;
        //newTag._out = new StringBuilder();
        return newTag;
    }

    @Override
    public void setRenderArgs(Map<String, Object> args) {
        super.setRenderArgs(args);
        if (args.containsKey("_body")) _body = (Body)args.get("_body");
    }

    @Override
    public void setRenderArg(String name, Object arg) {
        if ("_body".equals(name)) _body = (Body)arg;
        super.setRenderArg(name, arg);
    }

    public TextBuilder setBodyContext(Body body) {
        this._context = body;
        return this;
    }

    @Override
    public void call(int line) {
        _line = line;
        if (null != _context) {
            _out = new StringBuilder();
            _context.p(S.raw(renderWithParent()));
        } else if (null != _caller && null != _out) {
            _caller.p(S.raw(renderWithParent())); // a real tag
        } else {
            render(); // an normal template
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

    protected void _pTagBody(ParameterList parameterList, StringBuilder out) {
        if (null == _body) return;
        _body.render(parameterList, out);
    }

    @Override
    protected void _pLayoutContent() {
        if (null != _body) _body.render(null, out());
        else super._pLayoutContent();
    }

    @Override
    public String getName() {
        return null;
    }

    public String str() {
        return Rythm.renderStr("@args com.greenlaw110.rythm.runtime.ITag tag; Tag[tag.getName()|tag.getClass()]", this);
    }
}
