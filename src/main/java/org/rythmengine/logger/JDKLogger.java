/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
