package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.parser.build_in.*;

public class Rythm extends DialectBase {

    public String id() {
        return "rythm";
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{AssignParser.class, ArgsParser.class, BreakParser.class, ContinueParser.class,
            CacheParser.class, CommentParser.class, DebugParser.class, DefTagParser.class, EscapeParser.class,
            ElseIfParser.class, ExitIfNoClassParser.class,BraceParser.class, LogTimeParser.class,
            InvokeParser.class, InvokeTagParser.class, NullableExpressionParser.class, ExpressionParser.class,
            ExtendsParser.class, ForEachParser.class, GetParser.class, IfParser.class, ImportParser.class,
            IncludeParser.class, InitCodeParser.class, RawParser.class, RenderBodyParser.class,
            RenderSectionParser.class, ReturnParser.class, SectionParser.class, SetParser.class, SimpleParser.class,
            TimestampParser.class, VerbatimParser.class};
    }

}
