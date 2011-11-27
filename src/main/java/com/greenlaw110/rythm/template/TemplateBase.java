package com.greenlaw110.rythm.template;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.greenlaw110.rythm.util.TextBuilder;


public abstract class TemplateBase extends TextBuilder implements ITemplate {
    
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
    
    protected final void addRenderSection(String name, String section) {
        renderSections.put(name, section);
    }
    
    protected final void addAllRenderSections(Map<String, String> sections) {
        renderSections.putAll(sections);
    }
    
    @Override
    public String render() {
        build();
        if (null != parent) {
            parent.setRenderBody(toString());
            parent.addAllRenderSections(renderSections);
            return parent.render();
        } else {
            return toString();
        }
    }
    
    public abstract TextBuilder build();
}
