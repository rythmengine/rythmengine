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

import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * classes extends JavaTagBase are not template based, it's kind of like FastTag in Play
 */
public abstract class JavaTagBase extends TagBase {
    protected ITag.ParameterList _params;
    protected Body _body;

    public void setRenderArgs(ITag.ParameterList params) {
        _params = null == params ? new ParameterList() : params;
        __renderArgs.putAll(params.asMap());
    }

    @Override
    public void setRenderArg(String name, Object val) {
        if ("_body".equals(name)) _body = (Body) val;
        super.setRenderArg(name, val);
    }

    @Override
    public TextBuilder build() {
        if (null == _params) _params = new ParameterList();
        call(_params, _body);
        return this;
    }

    @Override
    protected void internalBuild() {
        build();
    }

    /**
     * Subclass overwrite this method and call various p() methods to render the output
     *
     * @param params
     * @param body
     */
    abstract protected void call(ITag.ParameterList params, Body body);
}
