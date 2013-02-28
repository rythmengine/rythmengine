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

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.Map;

/**
 * Define a tag
 */
public abstract class TagBase extends TemplateBase implements ITag {
    protected ILogger logger = Logger.get(TagBase.class);

    protected Body _body;

    protected Body _context;

    private int _line;

    protected int _line() {
        return _line;
    }

    @Override
    public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
        Map<String, String> m = null;
        TagBase newTag = (TagBase) super.cloneMe(engine, caller);
        newTag._body = null;
        //newTag.__buffer = new StringBuilder();
        return newTag;
    }

    @Override
    public void setRenderArgs(Map<String, Object> args) {
        super.setRenderArgs(args);
        if (args.containsKey("_body")) _body = (Body) args.get("_body");
    }

    @Override
    public void setRenderArg(String name, Object arg) {
        if ("_body".equals(name)) _body = (Body) arg;
        super.setRenderArg(name, arg);
    }

    public TextBuilder setBodyContext(Body body) {
        this._context = body;
        return this;
    }

    @Override
    public void call(int line) {
        _line = line;
        if (null != _context) {
            __buffer = new StringBuilder();
            _context.p(S.raw(renderWithParent()));
        } else if (null != __caller && null != __buffer) {
            __caller.p(S.raw(renderWithParent())); // a real tag
        } else {
            render(); // an normal template
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

    protected void _pTagBody(ParameterList parameterList, StringBuilder out) {
        if (null == _body) return;
        _body.render(parameterList, out);
    }

    @Override
    protected void _pLayoutContent() {
        if (null != _body) _body.render(null, buffer());
        else super._pLayoutContent();
    }

    @Override
    public String getName() {
        return null;
    }

    public String str() {
        return Rythm.renderStr("@args com.greenlaw110.rythm.template.ITag tag; Tag[tag.getName()|tag.getClass()]", this);
    }
}
