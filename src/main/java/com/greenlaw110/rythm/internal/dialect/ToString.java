package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.IContext;
import com.greenlaw110.rythm.internal.IDialect;
import com.greenlaw110.rythm.internal.ToStringTemplateBase;

/**
 * ToString mode is a very limited subset of Rythm which has only basic Rythm features:
 * <ul>
 * <li>Expression evaluation and escaping</li>
 * <li>if-elseif-else</li>
 * </ul>
 * <p/>
 * Specifically, argument declaration and scripting is disabled in ToString mode; @for loop is not allowed in ToString mode also
 */
public class ToString extends SimpleRythm {

    public static final String ID = "rythm-toString";

    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new ToString();

    protected ToString() {
    }

    public String a() {
        return "@";
    }

    protected Class type = null;

    public ToString(Class type) {
        if (null == type) throw new NullPointerException();
        this.type = type;
    }

    @Override
    public boolean isMyTemplate(String template) {
        String[] forbidden = {
                "@args",
                "@assign",
                "@debug",
                "@doLayout",
                "@doBody",
                "@extends",
                "@section",
                "@render",
                "@import",
                "@include",
                "@invoke",
                "@set",
                "@get",
                "@init",
                "@expand",
                "@exec",
                "@macro",
                "@compact",
                "@nocompact",
                "@def ",
                "@tag ",
                "@return",
                "@nosim",
                "@verbatim"
        };
        for (String s : forbidden) {
            if (template.contains(s)) return false;
        }

        return true;
    }

    @Override
    public boolean enableScripting() {
        return false;
    }

    @Override
    public void begin(IContext ctx) {
        CodeBuilder cb = ctx.getCodeBuilder();
        cb.addRenderArgs(ctx.currentLine(), type.getName().replace('$', '.'), "_");
        //cb.setSimpleTemplate(0);
        cb.setExtended(ToStringTemplateBase.class);
    }
}
