/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.dialect;

import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.parser.build_in.*;

/**
 * Basic Rythm is a very limited subset of Rythm which has only basic Rythm features:
 * <ul>
 * <li>Expression evaluation (nullable express also supported) and escaping</li>
 * <li>if-elseif-else, and for(T e: Iterable<T>)</></li>
 * </ul>
 * <p/>
 * Specifically, argument declaration and scripting is disabled in ToString mode; @for(; ;) is not allowed in Basic
 * mode to prevent infinite loop
 */
public class BasicRythm extends SimpleRythm {

    public static final String ID = "rythm-basic";

    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new BasicRythm();

    protected BasicRythm() {
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{BreakParser.class, ContinueParser.class, CommentParser.class, EscapeParser.class, ElseForParser.class, ElseIfParser.class, BraceParser.class, InvokeTemplateParser.class, NullableExpressionParser.class, ExpressionParser.class, ForEachParser.class, IfParser.class, RawParser.class, TimestampParser.class};
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
                "@inherited",
                "@i18n",
                "@import",
                "@include",
                "@invoke",
                "@locale",
                "@set",
                "@get",
                "@init",
                "@finally",
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
    public boolean enableFreeForLoop() {
        return false;
    }
}
