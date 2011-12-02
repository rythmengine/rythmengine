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
        return new Class<?>[]{ArgsParser.class, CommentParser.class, ElseIfParser.class,
                ExpressionParser.class, ForEachParser.class, IfParser.class,
                ImportParser.class};
    }

}
