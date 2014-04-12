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
package org.rythmengine.internal.dialect;

import org.rythmengine.internal.CodeBuilder;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.ToStringTemplateBase;

/**
 * ToString mode is a very limited subset of Rythm which has only basic Rythm features:
 * <ul>
 * <li>Expression evaluation and escaping</li>
 * <li>if-elseif-else</li>
 * </ul>
 * <p/>
 * Specifically, argument declaration and scripting is disabled in ToString mode; @for loop is not allowed in ToString mode also
 */
public class ToString extends SimpleRythm {

    public static final String ID = "rythm-toString";

    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new ToString();

    protected ToString() {
    }

    public String a() {
        return "@";
    }

    protected Class type = null;

    public ToString(Class type) {
        if (null == type) throw new NullPointerException();
        this.type = type;
    }

    @Override
    public boolean isMyTemplate(String template) {
        String[] forbidden = {
                "@args",
                "@assign",
                "@debug",
                "@doLayout",
                "@doBody",
                "@extends",
                "@section",
                "@render",
                "@inherited",
                "@import",
                "@include",
                "@invoke",
                "@set",
                "@get",
                "@init",
                "@expand",
                "@exec",
                "@macro",
                "@compact",
                "@nocompact",
                "@def ",
                "@tag ",
                "@return",
                "@nosim",
                "@verbatim"
        };
        for (String s : forbidden) {
            if (template.contains(s)) return false;
        }

        return true;
    }

    @Override
    public boolean enableScripting() {
        return false;
    }

    @Override
    public void begin(IContext ctx) {
        CodeBuilder cb = ctx.getCodeBuilder();
        cb.addRenderArgs(ctx.currentLine(), type.getName().replace('$', '.'), "_");
        //cb.setSimpleTemplate(0);
        cb.setExtended(ToStringTemplateBase.class);
    }
}
