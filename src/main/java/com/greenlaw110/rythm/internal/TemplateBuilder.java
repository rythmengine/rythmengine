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

import com.greenlaw110.rythm.utils.Escape;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.greenlaw110.rythm.utils.RawData;

/**
 * A special TextBuilder provides additional print utilities for expressions
 */
public class TemplateBuilder extends TextBuilder {

    /**
     * Return the default {@link com.greenlaw110.rythm.utils.Escape escape method}.
     * <p/>
     * <p>This implementation returns {@link com.greenlaw110.rythm.utils.Escape#RAW}.
     * But the sub class could override this method to return different escape method</p>
     *
     * @return {@link com.greenlaw110.rythm.utils.Escape escape}
     */
    protected Escape __defaultEscape() {
        return Escape.RAW;
    }

    // --- print expression interface

    /**
     * Print a char expression. same effect as {@link #p(char)}
     *
     * @param c
     * @return the current builder
     */
    public final TextBuilder pe(char c) {
        return p(c);
    }

    /**
     * Print a byte expression. same effect as {@link #p(byte)}
     *
     * @param b
     * @return the current builder
     */
    public final TextBuilder pe(byte b) {
        return p(b);
    }

    /**
     * Print an integer expression. same effect as {@link #p(int)}
     *
     * @param i
     * @return the current builder
     */
    public final TextBuilder pe(int i) {
        return p(i);
    }

    /**
     * Print a long expression. same effect as {@link #p(long)}
     *
     * @param l
     * @return the current builder
     */
    public final TextBuilder pe(long l) {
        return p(l);
    }

    /**
     * Print a float expression. same effect as {@link #p(float)}
     *
     * @param f
     * @return the current builder
     */
    public final TextBuilder pe(float f) {
        return p(f);
    }

    /**
     * Print a double. same effect as {@link #p(double)}
     *
     * @param d
     * @return the current builder
     */
    public final TextBuilder pe(double d) {
        return p(d);
    }

    /**
     * Print a boolean expression. same effect as {@link #p(boolean)}
     *
     * @param b
     * @return the current builder
     */
    public final TextBuilder pe(boolean b) {
        return p(b);
    }

    /**
     * Print a general expression with {@link #__defaultEscape() default escape} method
     *
     * @param o
     * @return the current builder
     */
    public TemplateBuilder pe(Object o) {
        return pe(o, null);
    }

    /**
     * Print a general expression, using specified 
     * {@link com.greenlaw110.rythm.utils.Escape escape method}
     *
     * @param o
     * @param escape
     * @return the current builder
     */
    public TemplateBuilder pe(Object o, Escape escape) {
        if (null != o) {
            if (o instanceof RawData) {
                return (TemplateBuilder) p(o);
            }
            if (null == escape) {
                escape = __defaultEscape();
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

    /**
     * See {@link #p(char)}
     */
    public final TextBuilder pe(char c, Escape escape) {
        return p(c);
    }

    /**
     * See {@link #p(int)}
     */
    public final TextBuilder pe(int i, Escape escape) {
        return p(i);
    }

    /**
     * See {@link #p(long)}
     */
    public final TextBuilder pe(long l, Escape escape) {
        return p(l);
    }

    /**
     * See {@link #p(float)}
     */
    public final TextBuilder pe(float f, Escape escape) {
        return p(f);
    }

    /**
     * See {@link #p(double)}
     */
    public final TextBuilder pe(double d, Escape escape) {
        return p(d);
    }

    /**
     * See {@link #p(boolean)}
     */
    public final TextBuilder pe(boolean b, Escape escape) {
        return p(b);
    }

}
