package com.greenlaw110.rythm.spi;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/03/12
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IExpressionProcessor {
    /**
     * Process the expression. Return true if processed false otherwise
     * @param exp
     * @param token
     * @return
     */
    boolean process(String exp, Token token);
}
