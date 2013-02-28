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
package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.internal.IDialect;
import com.greenlaw110.rythm.internal.parser.build_in.*;

/**
 * Basic Rythm is a very limited subset of Rythm which has only basic Rythm features:
 * <ul>
 * <li>Expression evaluation (nullable express also supported) and escaping</li>
 * <li>if-elseif-else, and for(T e: Iterable<T>)</></li>
 * </ul>
 * <p/>
 * Specifically, argument declaration and scripting is disabled in ToString mode; @for(; ;) is not allowed in Basic
 * mode to prevent infinite loop
 */
public class BasicRythm extends SimpleRythm {

    public static final String ID = "rythm-basic";

    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new BasicRythm();

    protected BasicRythm() {
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{BreakParser.class, ContinueParser.class, CommentParser.class, EscapeParser.class, ElseForParser.class, ElseIfParser.class, BraceParser.class, InvokeTemplateParser.class, NullableExpressionParser.class, ExpressionParser.class, ForEachParser.class, IfParser.class, RawParser.class, TimestampParser.class};
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
    public boolean enableFreeForLoop() {
        return false;
    }
}
