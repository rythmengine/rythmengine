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
        return new Class<?>[]{ArgsParser.class, CommentParser.class, DebugParser.class, DefTagParser.class,
                ElseIfParser.class, BraceParser.class, InvokeTagParser.class, ExpressionParser.class, ExtendsParser.class,
                ForEachParser.class, GetParser.class, IfParser.class, ImportParser.class, InitCodeParser.class,
                RenderBodyParser.class, RenderSectionParser.class, SectionParser.class, SetParser.class, VerbatimParser.class};
    }

}
