package com.greenlaw110.rythm.extension;

/**
 * A <code>IByteCodeHelper</code> could be plugged into {@link com.greenlaw110.rythm.RythmEngine}
 * in memory compilation system to provide extra way to locate class byte
 * code.
 * <p/>
 * <p>A usage example of <code>IByteCodeHelper</code> could be find in
 * Play!framework's Rythm plugin, which locates Play!Framework's
 * application classes when compiling template classes</p>
 * <p/>
 * <p>One {@link com.greenlaw110.rythm.RythmEngine engine instance} can have zero
 * or one <code>IByteCodeHelper</code></p>
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
