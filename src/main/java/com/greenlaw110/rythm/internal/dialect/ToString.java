package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.spi.IContext;

/**
 * ToString dialect is a kind of Rythm dialect, the difference is that
 * it preset the type of the only one render arg
 */
public class ToString extends Rythm {

    @Override
    public String id() {
        return "toString";
    }

    private Class type = null;

    public ToString(Class type) {
        if (null == type) throw new NullPointerException();
        this.type = type;
    }

    @Override
    public void begin(IContext ctx) {
        CodeBuilder cb = ctx.getCodeBuilder();
        cb.addRenderArgs(type.getName(), "_");
        cb.setSimpleTemplate(0);
    }
}
