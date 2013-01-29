package com.greenlaw110.rythm.spi;

/**
 * Define the service a template preprocessor needs to provide
 */
public interface ITemplatePreProcessor {
    /**
     * Process the template source and return
     * processed version
     * 
     * @param source
     * @return
     */
    String process(String source);
}
