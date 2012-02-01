package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.runtime.ITag;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 25/01/12
 * Time: 12:16 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TagBase extends TemplateBase implements ITag {

    protected Body _body;

    @Override
    public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
        TagBase newTag = (TagBase)super.cloneMe(engine, caller);
        newTag._body = null;
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

    public String str() {
        return Rythm.renderStr("@args com.greenlaw110.rythm.runtime.ITag tag; Tag[tag.getName()|tag.getClass()]", this);
    }
}
