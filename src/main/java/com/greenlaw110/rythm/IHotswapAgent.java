package com.greenlaw110.rythm;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

/**
 * An instance of <code>IHostswapAgent</code>   
 */
public interface IHotswapAgent {
    void reload(ClassDefinition... definitions) throws UnmodifiableClassException, ClassNotFoundException;
}
