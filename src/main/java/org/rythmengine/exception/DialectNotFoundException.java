/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.exception;

public class DialectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DialectNotFoundException(String id) {
        super(String.format("Dialect[%s] not found", id));
    }

}
