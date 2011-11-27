package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.internal.dialect.DialectBase;

public interface IParserFactory {
    IParser create(DialectBase dialect, IContext ctx);
}
