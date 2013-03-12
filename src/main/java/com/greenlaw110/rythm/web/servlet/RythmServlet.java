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
package com.greenlaw110.rythm.web.servlet;

import javax.servlet.http.HttpServlet;

/**
 * <p>A servlet to process Rythm templates. This is comparable to the
 * the JspServlet for JSP-based applications.</p>
 *
 * <p>The servlet provides the following features:</p>
 * <ul>
 *   <li>renders Rythm templates</li>
 *   <li>provides transparent access to the servlet request attributes,
 *       servlet session attributes and servlet context attributes by
 *       auto-searching them</li>
 *   <li>logs to the logging facility of the servlet API</li>
 * </ul>
 *
 * <p>RythmViewServlet supports the following configuration parameters
 * in web.xml:</p>
 * <dl>
 *   <dt>com.greenlaw110.rythm.properties</dt>
 *   <dd>Path and name of the Rythm configuration file. The path must be
 *     relative to the web application root directory. If this parameter
 *     is not present, Rythm will check for a properties file at
 *     '/WEB-INF/rythm.properties'.  If no file is found there, then
 *     Rythm is initialized with the settings in the classpath at
 *     'com.greenlaw110.rythm.web.servlet.properties'.</dd>
 *   <dt>com.greenlaw110.rythm.tools.shared.config</dt>
 *   <dd>By default, this is {@code true}. If set to {@code false}, then
 *     the {@link RythmView} used by this servlet will not be shared
 *     with {@link RythmViewFilter}s, other RythmViewServlets or 
 *     {@link com.greenlaw110.rythm.tools.view.jsp.RythmViewTag}s in the
 *     application.</dd>
 *   <dt>com.greenlaw110.rythm.tools.loadDefaults</dt>
 *   <dd>By default, this is {@code true}. If set to {@code false}, then
 *     the default toolbox configuration will not be added to your (if any)
 *     custom configuration.  NOTE: The default configuration will also be
 *     suppressed if you are using a deprecated toolbox.xml format and do not
 *     explicitly set this to {@code true}.</dd>
 *   <dt>com.greenlaw110.rythm.tools.cleanConfiguration</dt>
 *   <dd>By default, this is {@code false}. If set to {@code true}, then
 *     then the final toolbox configuration (the combination of any custom
 *     one(s) provided by yourself and/or the default configuration(s))
 *     will have all invalid tools, properties, and/or data removed prior to
 *     configuring the ToolboxFactory for this servlet by a
 *     {@link com.greenlaw110.rythm.tools.config.ConfigurationCleaner}</dd>
 *   <dt>com.greenlaw110.rythm.tools.bufferOutput</dt>
 *   <dd>By default, the processed templates are merged directly into
 *     the {@link HttpServletResponse}'s writer.  If this parameter is
 *     set to {@code true}, then the output of the merge process will be
 *     buffered before being fed to the response. This allows the {@link #error}
 *     method to be overridden to return a "500 Internal Server Error" or
 *     at least not return any of the failed request content. Essentially,
 *     setting this to {@code true} degrades performance in order to enable
 *     a more "correct" error response"</dd>
 *  </dd>
 * </dl>
 *
 */
public class RythmServlet extends HttpServlet {
    
}
