/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

import org.rythmengine.template.TemplateBase;

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
     * @return true if exception is handled
     */
    boolean handleTemplateExecutionException(Exception e, TemplateBase template);
}
