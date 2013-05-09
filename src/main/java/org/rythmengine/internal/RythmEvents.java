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
package org.rythmengine.internal;

import org.rythmengine.template.ITemplate;
import org.rythmengine.template.TemplateBase;
import org.rythmengine.utils.F;

/**
 * Built in {@link IEvent event}s
 */
public class RythmEvents<RETURN, PARAM> implements IEvent<RETURN, PARAM> {
    /**
     * Right before template parsing started
     */
    public static final
    IEvent<String, CodeBuilder> ON_PARSE = new RythmEvents<String, CodeBuilder>();

    /**
     * Before start building java source code. A good place to inject implicit
     * imports and render args
     */
    public static final
    IEvent<Void, CodeBuilder> ON_BUILD_JAVA_SOURCE = new RythmEvents<Void, CodeBuilder>();

    /**
     * Before close generated java source code class. A good place to inject
     * implicit java source
     */
    public static final
    IEvent<Void, CodeBuilder> ON_CLOSING_JAVA_CLASS = new RythmEvents<Void, CodeBuilder>();

    /**
     * Immediately after template get parsed and before final template java
     * source code generated
     */
    public static final
    IEvent<Void, CodeBuilder> PARSED = new RythmEvents<Void, CodeBuilder>();

    /**
     * Right before template compilation started
     */
    public static final
    IEvent<Void, String> ON_COMPILE = new RythmEvents<Void, String>();

    /**
     * Immediately after template compilation finished and before get cached on disk
     */
    public static final
    IEvent<Void, String> COMPILED = new RythmEvents<Void, String>();

    /**
     * Before template render start. A good place to set implicit render args
     */
    public static final
    IEvent<Void, ITemplate> ON_RENDER = new RythmEvents<Void, ITemplate>();

    /**
     * After template rendered.
     */
    public static final
    IEvent<Void, ITemplate> RENDERED = new RythmEvents<Void, ITemplate>();

    /**
     * Before tag invocation
     */
    public static final
    IEvent<Void, F.T2<TemplateBase, ITemplate>> ON_TAG_INVOCATION = new RythmEvents<Void, F.T2<TemplateBase, ITemplate>>();

    /**
     * Before tag invocation
     */
    public static final
    IEvent<Void, F.T2<TemplateBase, ITemplate>> TAG_INVOKED = new RythmEvents<Void, F.T2<TemplateBase, ITemplate>>();

    /**
     * Render execution exception captured
     */
    public static final
    IEvent<Boolean, F.T2<TemplateBase, Exception>> ON_RENDER_EXCEPTION = new RythmEvents<Boolean, F.T2<TemplateBase, Exception>>();

    @Override
    public RETURN trigger(IEventDispatcher eventBus, PARAM eventParam) {
        return (RETURN) eventBus.accept(this, eventParam);
    }
}
