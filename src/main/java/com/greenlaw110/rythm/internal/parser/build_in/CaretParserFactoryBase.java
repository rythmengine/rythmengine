package com.greenlaw110.rythm.internal.parser.build_in;

import com.greenlaw110.rythm.spi.ICaretParserFactory;
import com.greenlaw110.rythm.spi.IDialect;

public abstract class CaretParserFactoryBase implements ICaretParserFactory {

    public String getCaret(IDialect dialect) {
        return dialect.a();
    }

}
