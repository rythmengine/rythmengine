package com.greenlaw110.rythm.extension;

import com.greenlaw110.rythm.template.TemplateBase;

/**
 * Use application or framework plugin based on Rythm could 
 * implement this interface to define how they want to handle 
 * template execution exception. For example Play-rythm plugin
 * implement this interface to capture <code>play.mvc.result.Result</code>
 * type exception as a solution to allow calling controller action
 * method directly from within a template
 */
public interface IRenderExceptionHandler {

    /**
     * Handle exception and return true if the exception is handled, 
     * false otherwise
     * 
     * @param e
     * @param template
     * @return
     */
    boolean handleTemplateExecutionException(Exception e, TemplateBase template);
}
