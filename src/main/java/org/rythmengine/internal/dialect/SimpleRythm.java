/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.dialect;

import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.parser.build_in.*;

/**
 * Simple Rythm mode is a subset of Rythm mode which has most Rythm feature except the template layout/extend features:
 * <ul>
 * <li>Extends a layout template or declare template section</li>
 * <li>Render sub template sections/content</li>
 * <li>@Designate inti code section to be put into parent template</li>
 * <li>Include another template at parsing time</li>
 * <li>Set/get template variable to be passed to parent template</li>
 * </ul>
 */
public class SimpleRythm extends DialectBase {

    public static final String ID = "rythm-simple";

    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new SimpleRythm();

    protected SimpleRythm() {
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{AssignParser.class, ArgsParser.class, BreakParser.class, ContinueParser.class, CacheParser.class, CommentParser.class, CompactParser.class, DebugParser.class, DefTagParser.class, EscapeParser.class, ElseForParser.class, ElseIfParser.class, ExecParser.class, ExitIfNoClassParser.class, BraceParser.class, LogTimeParser.class, InvokeParser.class, InvokeMacroParser.class, InvokeTemplateParser.class, MacroParser.class, NullableExpressionParser.class, ExpressionParser.class, ForEachParser.class, I18nParser.class, IfParser.class, ImportParser.class, LocaleParser.class, NoCompactParser.class, RawParser.class, ReturnParser.class, ReturnIfParser.class, SimpleParser.class, TimestampParser.class, VerbatimParser.class};
    }

    @Override
    public boolean isMyTemplate(String template) {
        String[] forbidden = {
                "@extends",
                "@section",
                "@render",
                "@inherited",
                "@doLayout",
                "@doBody",
                "@include",
                "@set",
                "@get",
                "@init",
                "@finally",
                "@nosim"
        };
        for (String s : forbidden) {
            if (template.contains(s)) return false;
        }

        return true;
    }

    @Override
    public void begin(IContext ctx) {
        //ctx.getCodeBuilder().setSimpleTemplate(0);
    }
}
