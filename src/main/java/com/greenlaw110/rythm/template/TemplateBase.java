package com.greenlaw110.rythm.template;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.utils.TextBuilder;
import sun.rmi.transport.ObjectTable;


public abstract class TemplateBase extends TextBuilder implements ITemplate {

    protected transient RythmEngine engine = null;
    
    protected Map<String, Object> _properties = new HashMap<String, Object>();

    protected RythmEngine _engine() {
        return null == engine ? Rythm.engine : engine;
    }
    
    protected void _invokeTag(String name) {
        _engine().invokeTag(name, this, null, null);
    }

    protected void _invokeTag(String name, ITag.ParameterList params) {
        _engine().invokeTag(name, this, params, null);
    }
    
    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body) {
        _engine().invokeTag(name, this, params, body);
    }

    /* to be used by dynamic generated sub classes */
    private String renderBody = "";
    private Map<String, String> renderSections = new HashMap<String, String>();
    private Map<String, Object> renderProperties = new HashMap<String, Object>();
    
    private TemplateBase parent = null;
    
    public TemplateBase() {
        super();
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
    private TextBuilder tmpCaller = null;
    
    protected void _startSection(String name) {
        if (null == name) throw new NullPointerException("section name cannot be null");
        if (null != tmpOut) throw new IllegalStateException("section cannot be nested");
        tmpCaller = _caller;
        _caller = null;
        tmpOut = _out;
        _out = new StringBuilder();
        section = name;
    }
    
    protected void _endSection() {
        if (null == tmpOut && null == tmpCaller) throw new IllegalStateException("section has not been started");
        addRenderSection(section, _out.toString());
        _out = tmpOut;
        _caller = tmpCaller;
        tmpOut = null;
        tmpCaller = null;
    }
    
    protected void _pSection(String name) {
        p(renderSections.get(name));
    }

    protected void _pBody() {
        p(renderBody);
    }

    private void addAllRenderSections(Map<String, String> sections) {
        if (null != sections) renderSections.putAll(sections);
    }
    
    private void addAllRenderProperties(Map<String, Object> properties) {
        if (null != properties) renderProperties.putAll(properties);
    }

    protected TemplateBase internalClone() {
        try {
            return (TemplateBase)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
        TemplateBase tmpl = internalClone();
        if (tmpl.parent != null) {
            tmpl.parent = (TemplateBase) tmpl.parent.cloneMe(engine, caller);
        }
        tmpl.engine = engine;
        //if (null != out) tmpl._out = out;
        if (null != caller) {
            tmpl._caller = (TextBuilder)caller;
        }
        if (null != _out) tmpl._out = new StringBuilder();
        return tmpl;
    }

    @Override
    public String render() {
        //_out.setLength(0);
        build();
        if (null != parent) {
            parent.setRenderBody(toString());
            parent.addAllRenderSections(renderSections);
            parent.addAllRenderProperties(renderProperties);
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
        _properties.putAll(args);
    }

    @Override
    public void setRenderArgs(Object... args) {
    }

    @Override
    public void setRenderArg(String name, Object arg) {
        _properties.put(name, arg);
    }
    
    protected final void _set(String name, Object arg) {
        setRenderArg(name, arg);
    }
    
    protected final TemplateBase caller() {
        return null == _caller ? null : (TemplateBase)_caller;
    }
    
    @Override
    public Object getRenderArg(String name) {
        Object val = _properties.get(name);
        return null != val ? val : (null != _caller ? caller().getRenderArg(name) : null);
    }
    
    protected final Object _get(String name) {
        return getRenderArg(name);
    }

    protected final <T> T _getAs(String name, Class<T> c) {
        Object o = getRenderArg(name);
        if (null == o) return null;
        return (T)o;
    }

    protected final Object _getRenderProperty(String name) {
        return renderProperties.get(name);
    }
    
    protected final <T> T _getRenderPropertyAs(String name, Class<T> c) {
        Object o = _getRenderProperty(name);
        if (null == o) return null;
        return (T)o;
    }
    
    protected final void _setRenderProperty(String name, Object val) {
        renderProperties.put(name, val);
    }
    
    @Override
    public Map<String, Object> getRenderArgs() {
        return new HashMap<String, Object>(_properties);
    }

    @Override
    public void setRenderArg(int position, Object arg) {
    }
    
    @Override
    public StringBuilder getOut() {
        return out();
    }
    
    @Override
    public void setOut(StringBuilder out) {
        if (null != _caller) ((ITemplate)_caller).setOut(out);
        else _out = out;
    }
    
    // --- debugging interface
    protected static ILogger _logger = Logger.get(TemplateBase.class);
}
