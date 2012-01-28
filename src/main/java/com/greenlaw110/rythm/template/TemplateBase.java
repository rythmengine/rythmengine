package com.greenlaw110.rythm.template;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.util.TextBuilder;


public abstract class TemplateBase extends TextBuilder implements ITemplate {

    protected transient RythmEngine engine = null;

    protected RythmEngine _engine() {
        return null == engine ? Rythm.engine : engine;
    }
    
    protected void _invokeTag(String name) {
        _engine().invokeTag(name, out(), null, null);
    }

    protected void _invokeTag(String name, ITag.ParameterList params) {
        _engine().invokeTag(name, out(), params, null);
    }
    
    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body) {
        _engine().invokeTag(name, out(), params, body);
    }

    /* to be used by dynamic generated sub classes */
    @SuppressWarnings("unused")
    private String renderBody = "";
    private Map<String, String> renderSections = new HashMap<String, String>();
    
    private TemplateBase parent = null;
    
    public TemplateBase() {
        Class<? extends TemplateBase> c = getClass();
        Class<?> pc = c.getSuperclass();
        if (TemplateBase.class.isAssignableFrom(pc) && !Modifier.isAbstract(pc.getModifiers())) {
            try {
                parent = (TemplateBase) pc.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    protected final void setRenderBody(String body) {
        renderBody = body;
    }
    
    private void addRenderSection(String name, String section) {
        renderSections.put(name, section);
    }
    
    private StringBuilder tmpOut = null;
    private String section = null;
    
    protected void _startSection(String name) {
        if (null == name) throw new NullPointerException("section name cannot be null");
        if (null != tmpOut) throw new IllegalStateException("section cannot be nested");
        tmpOut = _out;
        _out = new StringBuilder();
        section = name;
    }
    
    protected void _endSection() {
        if (null == tmpOut) throw new IllegalStateException("section has not been started");
        addRenderSection(section, _out.toString());
        _out = tmpOut;
        tmpOut = null;
    }
    
    protected void _pSection(String name) {
        p(renderSections.get(name));
    }

    protected void _pBody() {
        p(renderBody);
    }

    private void addAllRenderSections(Map<String, String> sections) {
        renderSections.putAll(sections);
    }

    protected TemplateBase internalClone() {
        try {
            return (TemplateBase)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public ITemplate cloneMe(RythmEngine engine, StringBuilder out) {
        TemplateBase tmpl = internalClone();
        tmpl.engine = engine;
        if (null != out) tmpl._out = out;
        return tmpl;
    }

    @Override
    public String render() {
        _out.setLength(0);
        build();
        if (null != parent) {
            parent.setRenderBody(toString());
            parent.addAllRenderSections(renderSections);
            parent.build();
            return parent.render();
        } else {
            return toString();
        }
    }
    
    public TextBuilder build() {
        return this;
    }

    @Override
    public void setRenderArgs(Map<String, Object> args) {
    }

    @Override
    public void setRenderArgs(Object... args) {
    }

    @Override
    public void setRenderArg(String name, Object arg) {
    }
    
    @Override
    public Object getRenderArg(String name) {
        return null;
    }

    @Override
    public Map<String, Object> getRenderArgs() {
        return null;
    }

    @Override
    public void setRenderArg(int position, Object arg) {
    }
}
