package com.greenlaw110.rythm.spi;


public interface IParserFactory {
    IParser create(IContext ctx);
}
