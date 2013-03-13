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

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.utils.S;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ExtensionManager {

    private final Set<IJavaExtension> _extensions = new HashSet<IJavaExtension>();
    private final RythmEngine engine;

    public ExtensionManager(RythmEngine engine) {
        if (null == engine) throw new NullPointerException();
        this.engine = engine;
    }

    /**
     * Add a Java extension
     *
     * @param javaExtension
     */
    public void registerJavaExtension(IJavaExtension javaExtension) {
        _extensions.add(javaExtension);
    }

    Iterable<IJavaExtension> javaExtensions() {
        return _extensions;
    }

    /**
     * Is a specified method name a java extension?
     *
     * @param s
     * @return true if the name is a java extension
     */
    public boolean isJavaExtension(String s) {
        for (IJavaExtension ext : _extensions) {
            if (S.isEqual(s, ext.methodName())) {
                return true;
            }
        }
        return false;
    }

    public ExtensionManager registerUserDefinedParsers(IParserFactory... parsers) {
        return registerUserDefinedParsers(null, parsers);
    }

    /**
     * Register a special case parser to a dialect
     * <p/>
     * <p>for example, the play-rythm plugin might want to register a special case parser to
     * process something like @{Controller.actionMethod()} or &{'MSG_ID'} etc to "japid"
     * and "play-groovy" dialects
     *
     * @param dialect
     * @param parsers
     */
    public ExtensionManager registerUserDefinedParsers(String dialect, IParserFactory... parsers) {
        engine.dialectManager().registerExternalParsers(dialect, parsers);
        return this;
    }

    private List<IExpressionProcessor> expressionProcessors = new ArrayList<IExpressionProcessor>();

    public ExtensionManager registerExpressionProcessor(IExpressionProcessor p) {
        if (!expressionProcessors.contains(p)) expressionProcessors.add(p);
        return this;
    }

    public Iterable<IExpressionProcessor> expressionProcessors() {
        return expressionProcessors;
    }

    private List<ICodeType> codeTypeList = new ArrayList<ICodeType>();

    public ExtensionManager registerCodeType(ICodeType type) {
        codeTypeList.add(type);
        return this;
    }

    public Iterable<ICodeType> templateLangs() {
        return codeTypeList;
    }

    public boolean hasTemplateLangs() {
        return !codeTypeList.isEmpty();
    }

}
