package com.greenlaw110.rythm.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 19/01/12
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class JDKLogger implements ILogger{
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
    public void trace(Throwable t, String msg, Object ... arg) {
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
    public void debug(Throwable t, String msg, Object ... arg) {
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
    public void info(Throwable t, String msg, Object ... arg) {
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
    public void warn(Throwable t, String msg, Object... arg) {
        log(Level.WARNING, t, msg, arg);
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
            logger.logp(l, className, null, m, t);
        }
    }

    protected void log(Level l, String f, Object... a) {
        if (logger.isLoggable(l)) {
            logger.logp(l, className, null, f, a);
        }
    }

    public static class Factory implements ILoggerFactory {
        @Override
        public ILogger getLogger(Class<?> clazz) {
            return new JDKLogger(clazz);
        }
    }
}
