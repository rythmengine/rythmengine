package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.CodeBuilder;
import com.greenlaw110.rythm.internal.parser.build_in.*;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IDialect;
import com.greenlaw110.rythm.template.ToStringTemplateBase;

/**
 * ToString mode is a very limited subset of Rythm which has only basic Rythm features:
 * <ul>
 * <li>Expression evaluation and escaping</li>
 * <li>if-elseif-else</li>
 * </ul>
 * 
 * Specifically, argument declaration and scripting is disabled in ToString mode; @for loop is not allowed in ToString mode also
 */
public class ToString extends BasicRythm {

    public static final String ID = "rythm-toString";
    
    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new ToString();
    
    protected ToString() {}

    public String a() {
        return "@";
    }

    protected Class type = null;

    public ToString(Class type) {
        if (null == type) throw new NullPointerException();
        this.type = type;
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{EscapeParser.class, ElseIfParser.class, BraceParser.class, ExpressionParser.class, IfParser.class, RawParser.class, TimestampParser.class};
    }

    @Override
    public boolean isMyTemplate(String template) {
        String[] forbidden = {
            "@args",
            "@assign",
            "@break",
            "@continue",
            "@debug",
            "@doLayout",
            "@doBody",
            "@extends",
            "@for",
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
        for (String s: forbidden) {
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
        cb.addRenderArgs(ctx.currentLine(), type.getName(), "_");
        //cb.setSimpleTemplate(0);
        cb.setExtended(ToStringTemplateBase.class);
    }
}
