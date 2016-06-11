/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.dialect;

import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.parser.build_in.*;

public class Rythm extends DialectBase {

    public static final String ID = "rythm-full";

    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new Rythm();

    protected Rythm() {
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{AssignParser.class, ArgsParser.class, BreakParser.class, ContinueParser.class, CacheParser.class, CommentParser.class, CompactParser.class, DebugParser.class, DefTagParser.class, EscapeParser.class, ElseForParser.class, ElseIfParser.class, ExecParser.class, ExitIfNoClassParser.class, BraceParser.class, LogTimeParser.class, InvokeParser.class, InvokeMacroParser.class, InvokeTemplateParser.class, MacroParser.class, NullableExpressionParser.class, ExpressionParser.class, ExtendsParser.class, ForEachParser.class, FinallyCodeParser.class, GetParser.class, I18nParser.class, IfParser.class, ImportParser.class, IncludeParser.class, InitCodeParser.class, LocaleParser.class, NoCompactParser.class, NoSIMParser.class, RawParser.class, RenderBodyParser.class, RenderInheritedParser.class, RenderSectionParser.class, ReturnParser.class, ReturnIfParser.class, SectionParser.class, SetParser.class, SimpleParser.class, TimestampParser.class, VerbatimParser.class};
    }

    public boolean isMyTemplate(String template) {
        return true; // default all template is Rythm template
    }

}
