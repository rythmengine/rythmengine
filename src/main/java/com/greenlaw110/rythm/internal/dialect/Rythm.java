package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.parser.build_in.ArgsParser;
import com.greenlaw110.rythm.internal.parser.build_in.ElseIfParser;
import com.greenlaw110.rythm.internal.parser.build_in.ExpressionParser;
import com.greenlaw110.rythm.internal.parser.build_in.ForEachParser;
import com.greenlaw110.rythm.internal.parser.build_in.IfParser;
import com.greenlaw110.rythm.internal.parser.build_in.ImportParser;

public class Rythm extends DialectBase {

    @Override
    public String id() {
        return "rythm";
    }

    @Override
    public String a() {
        return "@";
    }

    @Override
    protected Class<?>[] buildInParserClasses() {
        return new Class<?>[] { ArgsParser.class, ElseIfParser.class,  
                ExpressionParser.class, ForEachParser.class, IfParser.class,  
                ImportParser.class};
    }

}
