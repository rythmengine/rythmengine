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

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 19/01/12
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ILogger {

    public boolean isTraceEnabled();

    public void trace(String format, Object... args);

    public void trace(Throwable t, String format, Object... args);

    public boolean isDebugEnabled();

    public void debug(String format, Object... args);

    public void debug(Throwable t, String format, Object... args);

    public boolean isInfoEnabled();

    public void info(String format, Object... arg);

    public void info(Throwable t, String format, Object... args);

    public boolean isWarnEnabled();

    public void warn(String format, Object... arg);

    public void warn(Throwable t, String format, Object... args);

    public boolean isErrorEnabled();

    public void error(String format, Object... arg);

    public void error(Throwable t, String format, Object... args);
}
