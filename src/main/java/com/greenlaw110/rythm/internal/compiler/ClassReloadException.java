package com.greenlaw110.rythm.internal.compiler;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/02/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassReloadException extends RuntimeException {
    public ClassReloadException(String msg) {
        super(msg);
    }
    public ClassReloadException(String msg, Throwable t) {
        super(msg, t);
    }
}
