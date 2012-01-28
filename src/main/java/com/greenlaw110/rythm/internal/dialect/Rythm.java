package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.parser.build_in.ArgsParser;
import com.greenlaw110.rythm.internal.parser.build_in.CommentParser;
import com.greenlaw110.rythm.internal.parser.build_in.DefTagParser;
import com.greenlaw110.rythm.internal.parser.build_in.ElseIfParser;
import com.greenlaw110.rythm.internal.parser.build_in.ExpressionParser;
import com.greenlaw110.rythm.internal.parser.build_in.ExtendsParser;
import com.greenlaw110.rythm.internal.parser.build_in.ForEachParser;
import com.greenlaw110.rythm.internal.parser.build_in.IfParser;
import com.greenlaw110.rythm.internal.parser.build_in.ImportParser;
import com.greenlaw110.rythm.internal.parser.build_in.InvokeTagParser;
import com.greenlaw110.rythm.internal.parser.build_in.RenderBodyParser;
import com.greenlaw110.rythm.internal.parser.build_in.RenderSectionParser;
import com.greenlaw110.rythm.internal.parser.build_in.SectionParser;

public class Rythm extends DialectBase {

    public String id() {
        return "rythm";
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        return new Class<?>[]{ArgsParser.class, CommentParser.class, DefTagParser.class, ElseIfParser.class,
                InvokeTagParser.class, ExpressionParser.class, ExtendsParser.class, ForEachParser.class, IfParser.class,
                ImportParser.class, RenderBodyParser.class, RenderSectionParser.class, SectionParser.class};
    }

}
