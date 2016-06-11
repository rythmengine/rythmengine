/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.template;

import org.rythmengine.utils.JSONWrapper;
import org.rythmengine.utils.TextBuilder;

import java.util.Map;

/**
 * Created by luog on 21/08/2014.
 */
public class EmptyTemplate extends TagBase {

    public static final EmptyTemplate INSTANCE = new EmptyTemplate();

    private EmptyTemplate() {}

    @Override
    public String __getName() {
        return "";
    }

    @Override
    public ITemplate __setRenderArgs(Map<String, Object> args) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(String name, Object arg) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(JSONWrapper jsonData) {
        return this;
    }

    @Override
    protected TemplateBase __setRenderArgs0(__ParameterList params) {
        return this;
    }

    @Override
    public ITemplate __setRenderArgs(Object... args) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(int position, Object arg) {
        return this;
    }

    @Override
    public TextBuilder build() {
        return this;
    }
}
