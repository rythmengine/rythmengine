package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.parser.build_in.*;

public class Rythm extends DialectBase {

    public String id() {
        return "rythm-full";
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{AssignParser.class, ArgsParser.class, BreakParser.class, ContinueParser.class, CacheParser.class, CommentParser.class, CompactParser.class, DebugParser.class, DefTagParser.class, EscapeParser.class, ElseForParser.class, ElseIfParser.class, ExecParser.class, ExpandParser.class, ExitIfNoClassParser.class, BraceParser.class, LogTimeParser.class, InvokeParser.class, InvokeMacroParser.class, InvokeTagParser.class, MacroParser.class, NullableExpressionParser.class, ExpressionParser.class, ExtendsParser.class, ForEachParser.class, GetParser.class, IfParser.class, ImportParser.class, IncludeParser.class, InitCodeParser.class, NoCompactParser.class, NoSIMParser.class, RawParser.class, RenderBodyParser.class, RenderSectionParser.class, ReturnParser.class, SectionParser.class, SetParser.class, SimpleParser.class, TimestampParser.class, VerbatimParser.class};
    }

    public boolean isMyTemplate(String template) {
        return true; // default all template is Rythm template
    }

}
