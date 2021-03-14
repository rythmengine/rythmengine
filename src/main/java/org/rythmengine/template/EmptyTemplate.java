/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.template;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.rythmengine.utils.JSONWrapper;
import org.rythmengine.utils.TextBuilder;

import java.util.Map;

/**
 * Created by luog on 21/08/2014.
 */
public class EmptyTemplate extends TagBase {

    public static final EmptyTemplate INSTANCE = new EmptyTemplate();

    private EmptyTemplate() {}

    @Override
    public String __getName() {
        return "";
    }

    @Override
    public ITemplate __setRenderArgs(Map<String, Object> args) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(String name, Object arg) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(JSONWrapper jsonData) {
        return this;
    }

    @Override
    protected TemplateBase __setRenderArgs0(__ParameterList params) {
        return this;
    }

    @Override
    public ITemplate __setRenderArgs(Object... args) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(int position, Object arg) {
        return this;
    }

    @Override
    public TextBuilder build() {
        return this;
    }
}
