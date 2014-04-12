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

import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.parser.build_in.*;

public class Rythm extends DialectBase {

    public static final String ID = "rythm-full";

    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new Rythm();

    protected Rythm() {
    }

    public String a() {
        return "@";
    }

    protected Class<?>[] buildInParserClasses() {
        // InvokeTagParse must be put in front of ExpressionParser as the later's matching pattern covers the former
        // BraceParser must be put in front of ElseIfParser
        return new Class<?>[]{AssignParser.class, ArgsParser.class, BreakParser.class, ContinueParser.class, CacheParser.class, CommentParser.class, CompactParser.class, DebugParser.class, DefTagParser.class, EscapeParser.class, ElseForParser.class, ElseIfParser.class, ExecParser.class, ExpandParser.class, ExitIfNoClassParser.class, BraceParser.class, LogTimeParser.class, InvokeParser.class, InvokeMacroParser.class, InvokeTemplateParser.class, MacroParser.class, NullableExpressionParser.class, ExpressionParser.class, ExtendsParser.class, ForEachParser.class, GetParser.class, I18nParser.class, IfParser.class, ImportParser.class, IncludeParser.class, InitCodeParser.class, LocaleParser.class, NoCompactParser.class, NoSIMParser.class, RawParser.class, RenderBodyParser.class, RenderInheritedParser.class, RenderSectionParser.class, ReturnParser.class, SectionParser.class, SetParser.class, SimpleParser.class, TimestampParser.class, VerbatimParser.class};
    }

    public boolean isMyTemplate(String template) {
        return true; // default all template is Rythm template
    }

}
