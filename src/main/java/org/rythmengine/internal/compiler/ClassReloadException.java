/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.compiler;

import org.rythmengine.exception.FastRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 18/02/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassReloadException extends FastRuntimeException {
    public ClassReloadException(String msg) {
        super(msg);
    }

    public ClassReloadException(String msg, Throwable t) {
        super(msg, t);
    }
}
