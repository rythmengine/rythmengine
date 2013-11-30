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
