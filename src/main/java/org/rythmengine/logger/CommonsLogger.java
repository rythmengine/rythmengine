/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Commons-logging logger implementation
 */
public class CommonsLogger implements ILogger {

    private static final long serialVersionUID = 1L;

    protected final Log logger;
    protected final String className;

    public CommonsLogger(Class c) {
        className = c.getName();
        logger = LogFactory.getLog(c);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    private String fmt(String tmpl, Object... args) {
        if (args.length == 0) {
            return tmpl;
        }
        return String.format(tmpl, args);
    }

    @Override
    public void trace(String msg, Object... arg) {
        logger.trace(fmt(msg, arg));
    }

    @Override
    public void trace(Throwable t, String msg, Object... arg) {
        logger.trace(fmt(msg, arg), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg, Object... arg) {
        logger.debug(fmt(msg, arg));
    }

    @Override
    public void debug(Throwable t, String msg, Object... arg) {
        logger.debug(fmt(msg, arg), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg, Object... arg) {
        logger.info(fmt(msg, arg));
    }

    @Override
    public void info(Throwable t, String msg, Object... arg) {
        logger.info(fmt(msg, arg), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg, Object... arg) {
        logger.warn(fmt(msg, arg));
    }

    @Override
    public void warn(Throwable t, String msg, Object... arg) {
        logger.warn(fmt(msg, arg), t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg, Object... arg) {
        logger.error(fmt(msg, arg));
    }

    @Override
    public void error(Throwable t, String msg, Object... arg) {
        logger.error(fmt(msg, arg), t);
    }

}
