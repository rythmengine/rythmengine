package com.greenlaw110.rythm.spi;

public interface IDialect {
    
    /**
     * Return the ID of the dialect, might be something like "rythm" or "play-groovy" etc.
     * @return
     */
    String id();
    
    /**
     * Register a special case parser which will be processed before all other parsers
     * 
     * <p>for example, the rythm extension for play!framework might want to register a special case parser to
     * process something like @{Controller.actionMethod()} or &{'MSG_ID'} etc.
     * 
     * @param parser
     */
    void registerSpecialParser(IParserFactory parser);
    
}
