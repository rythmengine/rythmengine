/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.logger;

import org.apache.log4j.BasicConfigurator;
import org.rythmengine.extension.ILoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A JDK logger implementation
 */
public class JDKLogger implements ILogger {
    private static final long serialVersionUID = 1L;
    protected final Logger logger;
    protected final String className;

    public JDKLogger(Class c) {
        className = c.getName();
        logger = Logger.getLogger(className);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public void trace(String msg, Object... arg) {
        log(Level.FINEST, msg, arg);
    }

    @Override
    public void trace(Throwable t, String msg, Object... arg) {
        log(Level.FINEST, t, msg, arg);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String msg, Object... arg) {
        log(Level.FINE, msg, arg);
    }

    @Override
    public void debug(Throwable t, String msg, Object... arg) {
        log(Level.FINE, t, msg, arg);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String msg, Object... arg) {
        log(Level.INFO, msg, arg);
    }

    @Override
    public void info(Throwable t, String msg, Object... arg) {
        log(Level.INFO, t, msg, arg);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String msg, Object... arg) {
        log(Level.WARNING, msg, arg);
    }

    @Override
    public void warn(Throwable t, String format, Object... arg) {
        log(Level.WARNING, t, format, arg);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String format, Object... arg) {
        log(Level.SEVERE, format, arg);
    }

    @Override
    public void error(Throwable t, String msg, Object... arg) {
        log(Level.SEVERE, t, msg, arg);
    }

    protected void log(Level l, Throwable t, String m, Object... a) {
        if (logger.isLoggable(l)) {
            try {
                m = String.format(m, a);
            } catch (Exception e) {
                // ignore 
            }
            logger.logp(l, className, null, m, t);
        }
    }

    protected void log(Level l, String m, Object... a) {
        if (logger.isLoggable(l)) {
            try {
                m = String.format(m, a);
            } catch (Exception e) {
                // ignore 
            }
            logger.logp(l, className, null, m);
        }
    }

    public static boolean firstCall=true;
    public static class Factory implements ILoggerFactory {
        @Override
        public ILogger getLogger(Class<?> clazz) {
            return new JDKLogger(clazz);
        }
    }
}
