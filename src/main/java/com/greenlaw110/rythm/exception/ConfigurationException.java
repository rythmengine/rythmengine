package com.greenlaw110.rythm.exception;

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
