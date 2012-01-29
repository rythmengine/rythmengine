package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.DialectNotFoundException;


public class ExtensionManager {

    private RythmEngine engine;

    public ExtensionManager(RythmEngine engine) {
        this.engine = engine;
    }

    RythmEngine engine() {
        return null == engine ? Rythm.engine : engine;
    }

    public void registerUserDefinedParsers(IParserFactory... parsers) {
        registerUserDefinedParsers(null, parsers);
    }

    /**
     * Register a special case parser to a dialect
     * 
     * <p>for example, the play-rythm plugin might want to register a special case parser to
     * process something like @{Controller.actionMethod()} or &{'MSG_ID'} etc to "japid" 
     * and "play-groovy" dialects
     * 
     * @param dialect
     * @param parsers
     */
    public void registerUserDefinedParsers(String dialect, IParserFactory... parsers) {
        IDialect d = engine().getDialectManager().get(dialect);
        if (null == d) throw new DialectNotFoundException(dialect);
        for (IParserFactory p: parsers) d.registerParserFactory(p);
    }
}
