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
        log(Level.FINEST, String.format(msg, arg));
    }

    @Override
    public void trace(Throwable t, String msg, Object ... arg) {
        log(Level.FINEST, t, String.format(msg, arg));
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String msg, Object... arg) {
        log(Level.FINE, String.format(msg, arg));
    }

    @Override
    public void debug(Throwable t, String msg, Object ... arg) {
        log(Level.FINE, t, String.format(msg, arg));
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String msg, Object... arg) {
        log(Level.INFO, String.format(msg, arg));
    }

    @Override
    public void info(Throwable t, String msg, Object ... arg) {
        log(Level.INFO, t, String.format(msg, arg));
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String msg, Object... arg) {
        log(Level.WARNING, String.format(msg, arg));
    }

    @Override
    public void warn(Throwable t, String msg, Object... arg) {
        log(Level.WARNING, t,String.format(msg, arg));
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String format, Object... arg) {
        log(Level.SEVERE, String.format(format, arg));

    }

    @Override
    public void error(Throwable t, String msg, Object... arg) {
        log(Level.SEVERE, t, String.format(msg, arg), arg);
    }

    protected void log(Level l, Throwable t, String m, Object... a) {
        if (logger.isLoggable(l)) {
            logger.logp(l, className, null, String.format(m, a), t);
        }
    }

    protected void log(Level l, String f, Object... a) {
        if (logger.isLoggable(l)) {
            logger.logp(l, className, null, String.format(f, a));
        }
    }

    public static class Factory implements ILoggerFactory {
        @Override
        public ILogger getLogger(Class<?> clazz) {
            return new JDKLogger(clazz);
        }
    }
}
