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
package com.greenlaw110.rythm.logger;

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
