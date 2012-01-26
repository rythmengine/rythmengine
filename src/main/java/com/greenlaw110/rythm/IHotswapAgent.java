package com.greenlaw110.rythm;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 23/01/12
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IHotswapAgent {
    void reload(ClassDefinition... definitions) throws UnmodifiableClassException, ClassNotFoundException;
}
