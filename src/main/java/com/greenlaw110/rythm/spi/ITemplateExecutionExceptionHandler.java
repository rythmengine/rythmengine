package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.template.TemplateBase;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/03/12
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITemplateExecutionExceptionHandler {
    /**
     * Return true if the exception is handled, false otherwise
     * @param e
     * @param template
     * @return
     */
    boolean handleTemplateExecutionException(Exception e, TemplateBase template);
}
