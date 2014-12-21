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

import org.rythmengine.internal.compiler.TemplateClass;
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
    IEvent<String, CodeBuilder> ON_PARSE = new RythmEvents<String, CodeBuilder>(true, 1);

    /**
     * Before start building java source code. A good place to inject implicit
     * imports and render args
     */
    public static final
    IEvent<Void, CodeBuilder> ON_BUILD_JAVA_SOURCE = new RythmEvents<Void, CodeBuilder>(true, 2);

    /**
     * Before close generated java source code class. A good place to inject
     * implicit java source
     */
    public static final
    IEvent<Void, CodeBuilder> ON_CLOSING_JAVA_CLASS = new RythmEvents<Void, CodeBuilder>(true, 3);

    /**
     * Immediately after template get parsed and before final template java
     * source code generated
     */
    public static final
    IEvent<Void, CodeBuilder> PARSED = new RythmEvents<Void, CodeBuilder>(true, 4);

    /**
     * Triggered upon parse exception
     */
    public static final
    IEvent<Void, TemplateClass> PARSE_FAILED = new RythmEvents<Void, TemplateClass>(true, 5);

    /**
     * Right before template compilation started
     */
    public static final
    IEvent<Void, String> ON_COMPILE = new RythmEvents<Void, String>(true, 6);

    /**
     * Immediately after template compilation finished and before get cached on disk
     * A good place to do byte code enhancement
     */
    public static final
    IEvent<byte[], byte[]> COMPILED = new RythmEvents<byte[], byte[]>(true, 7);

    /**
     * Triggered upon compilation of a template class failed
     */
    public static final
    IEvent<Void, TemplateClass> COMPILE_FAILED = new RythmEvents<Void, TemplateClass>(true, 8);

    /**
     * Before template render start. A good place to set implicit render args
     */
    public static final
    IEvent<Void, ITemplate> ON_RENDER = new RythmEvents<Void, ITemplate>(false, 9);

    /**
     * After template rendered.
     */
    public static final
    IEvent<Void, ITemplate> RENDERED = new RythmEvents<Void, ITemplate>(true, 10);

    /**
     * Before tag invocation
     */
    public static final
    IEvent<Void, F.T2<TemplateBase, ITemplate>> ON_TAG_INVOCATION = new RythmEvents<Void, F.T2<TemplateBase, ITemplate>>(false, 11);

    /**
     * Triggered immediately when RythmEngine.invokeTemplate() method get called
     */
    public static final
    IEvent<Void, TemplateBase> ENTER_INVOKE_TEMPLATE = new RythmEvents<Void, TemplateBase>(false, 12);

    /**
     * Triggered after RythmEngine.invokeTemplate() method get called
     */
    public static final
    IEvent<Void, TemplateBase> EXIT_INVOKE_TEMPLATE = new RythmEvents<Void, TemplateBase>(false, 13);

    /**
     * Before tag invocation
     */
    public static final
    IEvent<Void, F.T2<TemplateBase, ITemplate>> TAG_INVOKED = new RythmEvents<Void, F.T2<TemplateBase, ITemplate>>(false, 14);

    /**
     * Render execution exception captured
     */
    public static final
    IEvent<Boolean, F.T2<TemplateBase, Exception>> ON_RENDER_EXCEPTION = new RythmEvents<Boolean, F.T2<TemplateBase, Exception>>(true, 0);
    
    private boolean safe = false;
    private int id;
    
    private RythmEvents(boolean isSafe, int id) {
        safe = isSafe;
        this.id = id;
    }
    
    public boolean isSafe() {
        return safe;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public RETURN trigger(IEventDispatcher eventBus, PARAM eventParam) {
        return (RETURN) eventBus.accept(this, eventParam);
    }
}
