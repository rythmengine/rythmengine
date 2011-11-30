package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.exception.DialectNotFoundException;


public class ExtensionManager {
    
    /**
     * Register a special case parser to a dialect
     * 
     * <p>for example, the play-rythm plugin might want to register a special case parser to
     * process something like @{Controller.actionMethod()} or &{'MSG_ID'} etc to "japid" 
     * and "play-groovy" dialects
     * 
     * @param regex
     * @param parser
     */
    public void registerSpecialCaseParser(String dialect, IParserFactory parser) {
        IDialect d = Rythm.getDialectManager().get(dialect);
        if (null == d) throw new DialectNotFoundException(dialect);
        d.registerParserFactory(parser);
    }
}
