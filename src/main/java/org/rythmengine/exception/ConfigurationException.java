/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.exception;

/**
 * The exception thrown out when Rythm configuration error
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String msg, Object... args) {
        super(String.format(msg, args));
    }

    public ConfigurationException(Exception e, String msg, Object... args) {
        super(String.format(msg, args), e);
    }
}
