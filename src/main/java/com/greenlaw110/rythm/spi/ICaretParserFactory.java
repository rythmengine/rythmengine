package com.greenlaw110.rythm.spi;

public interface ICaretParserFactory extends IParserFactory {
    String getCaret(IDialect dialect);
}
