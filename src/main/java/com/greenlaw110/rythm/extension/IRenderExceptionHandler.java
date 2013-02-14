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
package com.greenlaw110.rythm.extension;

import com.greenlaw110.rythm.template.TemplateBase;

/**
 * Use application or framework plugin based on Rythm could
 * implement this interface to define how they want to handle
 * template execution exception. For example Play-rythm plugin
 * implement this interface to capture <code>play.mvc.result.Result</code>
 * type exception as a solution to allow calling controller action
 * method directly from within a template
 */
public interface IRenderExceptionHandler {

    /**
     * Handle exception and return true if the exception is handled,
     * false otherwise
     *
     * @param e
     * @param template
     * @return
     */
    boolean handleTemplateExecutionException(Exception e, TemplateBase template);
}
