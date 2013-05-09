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

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.AutoToStringCodeBuilder;
import org.rythmengine.internal.CodeBuilder;
import org.rythmengine.internal.IDialect;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.toString.ToStringOption;
import org.rythmengine.toString.ToStringStyle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ToString dialect is a kind of Rythm dialect, the difference is that
 * it preset the type of the only one render arg
 */
public class AutoToString extends ToString {

    public static final String ID = "rythm-autoToString";

    @Override
    public String id() {
        return ID;
    }

    public static final IDialect INSTANCE = new AutoToString();

    protected AutoToString() {
    }

    public AutoToStringData meta = null;

    public AutoToString(Class type, AutoToStringData data) {
        super(type);
        meta = data;
    }

    @Override
    public CodeBuilder createCodeBuilder(String template, String className, String tagName, TemplateClass templateClass, RythmEngine engine) {
        return new AutoToStringCodeBuilder(template, className, tagName, templateClass, engine, this);
    }

    public static String templateStr(Class<?> c, ToStringOption o, ToStringStyle s) {
        return String.format("{class: %s; toStringOption: %s; toStringStyle: %s}", null == c ? "" : c.getName(), o.toString(), s.toString());
    }

    public static class AutoToStringData {

        public AutoToStringData(Class<?> clazz, ToStringOption option, ToStringStyle style) {
            this.clazz = clazz;
            if (null != option) this.option = option;
            if (null != style) this.style = style;
        }

        public Class<?> clazz;
        public ToStringOption option = ToStringOption.DEFAULT_OPTION;
        public ToStringStyle style = ToStringStyle.DEFAULT_STYLE;

        private int hash = 0;

        @Override
        public String toString() {
            return templateStr(clazz, option, style);
        }

        @Override
        public int hashCode() {
            if (0 == hash) hash = ((31 + clazz.hashCode()) * 17 + option.hashCode()) * 17 + style.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof AutoToStringData) {
                AutoToStringData that = (AutoToStringData) obj;
                return that.clazz.equals(this.clazz) && that.option.equals(this.option) && that.style.equals(this.style);
            }
            return false;
        }

        public static AutoToStringData valueOf(String s) {
            return parseStr(s);
        }
    }

    private static final Pattern P = Pattern.compile("\\{class *: *([a-zA-Z_0-9\\.\\$]+) *; *toStringOption *: *(\\{.*?\\}) *; *toStringStyle *: *([a-zA-Z_0-9\\.\\$]+) *\\}");

    public static AutoToStringData parseStr(String s) {
        Matcher m = P.matcher(s);
        if (!m.matches()) throw new IllegalArgumentException("Unrecognized AutoToString template: " + s);
        String cs = m.group(1);
        String os = m.group(2);
        String ss = m.group(3);
        Class<?> c;
        RythmEngine engine = RythmEngine.get();
        if (null == engine) {
            engine = org.rythmengine.Rythm.engine();
        }
        try {
            c = engine.classLoader().loadClass(cs);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + cs);
        }

        ToStringOption o = ToStringOption.valueOf(os);
        ToStringStyle st = ToStringStyle.valueOf(ss);
        return new AutoToStringData(c, o, st);
    }
}
