package com.greenlaw110.rythm.extension;

/**
 * Use application or framework plugins based on rythm engine could
 * implement this interface to allow further process compiled
 * template classes.
 * 
 * <p>One {@link com.greenlaw110.rythm.RythmEngine engine instance} can have zero
 * or one <code>ITemplateClassEnhancer</code></p>
 */
public interface IByteCodeEnhancer {
    /**
     * Enhance byte code. This method is called after a template
     * class get compiled and before it is cached to disk
     *
     * @param className
     * @param classBytes
     * @return
     * @throws Exception
     */
    byte[] enhance(String className, byte[] classBytes) throws Exception;

}
