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

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;

/**
 * Define interface for customized i18n message resolver
 */
public interface II18nMessageResolver {

    /**
     * <p>Return i18n message of a given key and args, use the locale info from the template specified. 
     * if <tt>null</tt> template passed in then it will try to guess from the current engine via
     * {@link com.greenlaw110.rythm.RythmEngine#get()}</p>
     * 
     * @param template
     * @param key
     * @param args the format arguments. If the first argument is of type Locale then it will be used to specify
     * the locale of the processing, and the rest elements are used as format arguments
     * @return the i18n message
     */
    String getMessage(ITemplate template, String key, Object... args);
    
    public static class DefaultImpl implements II18nMessageResolver {

        public static II18nMessageResolver INSTANCE = new DefaultImpl();
    
        private DefaultImpl() {}
    
        @Override
        public String getMessage(ITemplate template, String key, Object... args) {
            return S.i18n(template, key, args);
        }
    }
}
