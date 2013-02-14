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
package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.extension.ILang;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage {@link com.greenlaw110.rythm.extension.ILang template language} implementations
 */
public class TemplateLangManager {
    private List<ILang> _templateLangList = new ArrayList<ILang>();

    public TemplateLangManager registerTemplateLang(ILang lang) {
        _templateLangList.add(lang);
        return this;
    }

    public Iterable<ILang> templateLangs() {
        return _templateLangList;
    }

    public boolean hasTemplateLangs() {
        return !_templateLangList.isEmpty();
    }
}
