/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/03/12
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IExpressionProcessor {
    /**
     * Process the expression. Return processed result if processed null otherwise
     *
     * @param exp
     * @param token
     * @return processed result
     */
    String process(String exp, Token token);

    public interface IResult {
        String get();
    }
}
