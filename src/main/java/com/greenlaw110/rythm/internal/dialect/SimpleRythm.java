package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.parser.build_in.*;
import com.greenlaw110.rythm.spi.IContext;

public class SimpleRythm extends DialectBase {

    public String id() {
        return "simple_rythm";
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{AssignParser.class, BreakParser.class, BraceParser.class, ContinueParser.class,
                CommentParser.class, DebugParser.class, EscapeParser.class, ElseIfParser.class, BraceParser.class,
                InvokeParser.class, InvokeTagParser.class, ExpressionParser.class, ForEachParser.class, IfParser.class,
                ImportParser.class, NoSIMParser.class, RawParser.class, ReturnParser.class, SimpleParser.class,
                TimestampParser.class, VerbatimParser.class};
    }

    @Override
    public boolean isMyTemplate(String template) {
        String[] forbidden = {
            "@args",
            "@extends",
            "@section",
            "@render",
            "@doLayout",
            "@doBody",
            "@include",
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
            "@nosim"
        };
        for (String s: forbidden) {
            if (template.contains(s)) return false;
        }

        return true;
    }

    @Override
    public void begin(IContext ctx) {
        ctx.getCodeBuilder().setSimpleTemplate(0);
    }
}
