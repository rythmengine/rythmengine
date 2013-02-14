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
package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

/**
 * A special TextBuilder provides additional print utilities for expressions
 */
public class TemplateBuilder extends TextBuilder {

    /**
     * Return the default {@link com.greenlaw110.rythm.template.ITemplate.Escape escape method}.
     * <p/>
     * <p>This implementation returns {@link com.greenlaw110.rythm.template.ITemplate.Escape#RAW}.
     * But the sub class could override this method to return different escape method</p>
     *
     * @return
     */
    protected ITemplate.Escape defaultEscape() {
        return ITemplate.Escape.RAW;
    }

    // --- print expression interface

    /**
     * Print a char expression. same effect as {@link #p(char)}
     *
     * @param c
     * @return
     */
    public final TextBuilder pe(char c) {
        return p(c);
    }

    /**
     * Print a byte expression. same effect as {@link #p(byte)}
     *
     * @param b
     * @return
     */
    public final TextBuilder pe(byte b) {
        return p(b);
    }

    /**
     * Print an integer expression. same effect as {@link #p(int)}
     *
     * @param i
     * @return
     */
    public final TextBuilder pe(int i) {
        return p(i);
    }

    /**
     * Print a long expression. same effect as {@link #p(long)}
     *
     * @param l
     * @return
     */
    public final TextBuilder pe(long l) {
        return p(l);
    }

    /**
     * Print a float expression. same effect as {@link #p(float)}
     *
     * @param f
     * @return
     */
    public final TextBuilder pe(float f) {
        return p(f);
    }

    /**
     * Print a double. same effect as {@link #p(double)}
     *
     * @param d
     * @return
     */
    public final TextBuilder pe(double d) {
        return p(d);
    }

    /**
     * Print a boolean expression. same effect as {@link #p(boolean)}
     *
     * @param b
     * @return
     */
    public final TextBuilder pe(boolean b) {
        return p(b);
    }

    /**
     * Print a general expression with {@link #defaultEscape() default escape} method
     *
     * @param o
     * @return
     */
    public TemplateBuilder pe(Object o) {
        return pe(o, null);
    }

    /**
     * Print a general expression, using specified {@link com.greenlaw110.rythm.template.ITemplate.Escape
     * escape method}
     *
     * @param o
     * @param escape
     * @return
     */
    public TemplateBuilder pe(Object o, ITemplate.Escape escape) {
        if (null != o) {
            if (o instanceof ITemplate.RawData) {
                return (TemplateBuilder) p(o);
            }
            if (null == escape) {
                escape = defaultEscape();
            }
            switch (escape) {
                case RAW:
                    return (TemplateBuilder) p(S.raw(o));
                case HTML:
                    return (TemplateBuilder) p(S.escapeHtml(o));
                case JSON:
                    return (TemplateBuilder) p(S.escapeJson(o));
                case JS:
                    return (TemplateBuilder) p(S.escapeJavaScript(o));
                case CSV:
                    return (TemplateBuilder) p(S.escapeCsv(o));
                case XML:
                    return (TemplateBuilder) p(S.escapeXml(o));
            }
        }
        return this;
    }

    public final TextBuilder pe(char c, ITemplate.Escape escape) {
        return p(c);
    }

    public final TextBuilder pe(int i, ITemplate.Escape escape) {
        return p(i);
    }

    public final TextBuilder pe(long l, ITemplate.Escape escape) {
        return p(l);
    }

    public final TextBuilder pe(float f, ITemplate.Escape escape) {
        return p(f);
    }

    public final TextBuilder pe(double d, ITemplate.Escape escape) {
        return p(d);
    }

    public final TextBuilder pe(boolean b, ITemplate.Escape escape) {
        return p(b);
    }

}
