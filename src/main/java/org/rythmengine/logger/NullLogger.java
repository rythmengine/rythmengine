/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.logger;

import org.rythmengine.extension.ILoggerFactory;

import java.util.logging.Level;

/**
 * A NULL logger implementation which log nothing
 */
public class NullLogger implements ILogger {
    private static final long serialVersionUID = 1L;

    public NullLogger(Class c) {
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg, Object... arg) {
    }

    @Override
    public void trace(Throwable t, String msg, Object... arg) {
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg, Object... arg) {
    }

    @Override
    public void debug(Throwable t, String msg, Object... arg) {
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg, Object... arg) {
    }

    @Override
    public void info(Throwable t, String msg, Object... arg) {
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg, Object... arg) {
    }

    @Override
    public void warn(Throwable t, String format, Object... arg) {
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String format, Object... arg) {
    }

    @Override
    public void error(Throwable t, String msg, Object... arg) {
    }

    protected void log(Level l, Throwable t, String m, Object... a) {
    }

    protected void log(Level l, String m, Object... a) {
    }

    public static class Factory implements ILoggerFactory {
        @Override
        public ILogger getLogger(Class<?> clazz) {
            return new NullLogger(clazz);
        }
    }
}
