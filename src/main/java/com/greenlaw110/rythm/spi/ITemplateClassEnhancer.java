package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ITemplateClassEnhancer {
    /**
     * Enhance byte code
     * @param className
     * @param classBytes
     * @return
     * @throws Exception
     */
    byte[] enhance(String className, byte[] classBytes) throws Exception;

    /**
     * Return source code to be added to template class
     */
    String sourceCode();
}
