package com.greenlaw110.rythm;

/**
 * A <code>IByteCodeHelper</code> could be plugged into {@link RythmEngine} 
 * in memory compilation system to provide extra way to locate class byte
 * code.
 * 
 * <p>A usage example of <code>IByteCodeHelper</code> could be find in
 * Play!framework's Rythm plugin, which locates Play!Framework's 
 * application classes when compiling template classes</p>
 */
public interface IByteCodeHelper {
    /**
     * Return the byte code of a class specified by the 
     * parameter
     * 
     * @param typeName The full name of the class who's byte code to be located
     * @return
     */
    byte[] findByteCode(String typeName);
}
