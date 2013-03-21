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
package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.IEvent;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.S;

import java.util.Map;

/**
 * Define a tag
 */
public abstract class TagBase extends TemplateBase implements ITag {
    protected ILogger logger = Logger.get(TagBase.class);

    protected __Body __body;
    
    protected __Body _body; // keep compatibility with previous version
    
    private void setBody(__Body b) {
        __body = b;
        _body = b;
    }

    protected __Body __context;

    private int __line;
    
    protected int __line() {
        return __line;
    }

    private boolean calling;

    /**
     * Check if inside a tag calling context 
     * 
     * <p>Note this is not an API for user application</p>
     * 
     * @return true if is inside a tag calling context
     */
    public boolean __calling() {
        return calling;
    }

    @Override
    public ITemplate __cloneMe(RythmEngine engine, ITemplate caller) {
        Map<String, String> m = null;
        TagBase newTag = (TagBase) super.__cloneMe(engine, caller);
        newTag.setBody(null);
        //newTag.__buffer = new StringBuilder();
        return newTag;
    }

    @Override
    public ITemplate __setRenderArgs(Map<String, Object> args) {
        super.__setRenderArgs(args);
        if (args.containsKey("__body")) setBody((__Body) args.get("__body"));
        return this;
    }

    @Override
    public ITemplate __setRenderArg(String name, Object arg) {
        if ("__body".equals(name)) setBody((__Body) arg);
        super.__setRenderArg(name, arg);
        return this;
    }

    @Override
    public ITag __setBodyContext(__Body body) {
        this.__context = body;
        return this;
    }
    
    protected void __triggerRenderEvent(IEvent<Void, ITemplate> event, RythmEngine engine) {
        if (calling) return; // do not trigger render events while calling as a tag
        event.trigger(engine, this);
    }

    @Override
    public void __call(int line) {
        __line = line;
        calling = true;
        try {
            if (null != __context) {
                __buffer = new StringBuilder();
                __context.p(S.raw(renderWithParent()));
            } else if (null != __caller && null != __buffer) {
                __caller.p(S.raw(renderWithParent())); // a real tag
            } else {
                render(); // an normal template
            }
        } finally {
            calling = false;
        }
    }

    // make sure it does not write to OutputStream or Writer
    private String renderWithParent() {
        if (null != __parent) return render();
        __parent = this;
        try {
            return render();
        } finally {
            __parent = null;
        }
    }

    protected void _pTagBody(__ParameterList parameterList, StringBuilder out) {
        if (null == __body) return;
        __body.render(parameterList, out);
    }

    @Override
    protected void __pLayoutContent() {
        if (null != __body) __body.render(null, buffer());
        else super.__pLayoutContent();
    }

    @Override
    public String __getName() {
        return null;
    }

    public String __str() {
        return String.format("Tag[%s|%s]", this.__getName(), this.getClass());
    }
}
