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
package org.rythmengine.utils;

import org.rythmengine.exception.FastRuntimeException;
import org.rythmengine.template.ITemplate;

/**
 * This class defines a chained text/string builder
 *
 * @author luog
 */
public class TextBuilder implements Cloneable {

    /**
     * The internal buffer
     */
    protected StringBuilder __buffer;

    /**
     * Return the internal buffer of the text builder.
     * If the buffer of this instance is null then return
     * the {@link #caller()} instance's buffer
     *
     * @return a <code>StringBuilder</code>
     */
    public StringBuilder buffer() {
        return null == __buffer ? __caller.buffer() : __buffer;
    }

    /**
     * The caller text builder. It is always an {@link ITemplate} instance
     */
    protected TextBuilder __caller;

    /**
     * Return the caller in {@link ITemplate} type
     *
     * @return a <code>ITemplate</code>
     */
    protected ITemplate caller() {
        return (ITemplate) __caller;
    }

    /**
     * Alias of {@link #buffer()}
     *
     * @return buffer as {@link StringBuilder}
     */
    public StringBuilder __getBuffer() {
        return buffer();
    }

    /**
     * Set the internal buffer to this instance or if the
     * {@link #caller()} exists, to the caller instance
     *
     * @param buffer
     */
    public void __setBuffer(StringBuilder buffer) {
        if (null != __caller) ((TextBuilder) __caller).__setBuffer(buffer);
        else __buffer = buffer;
    }

    /**
     * Set the internal buffer to this instance
     *
     * @param buffer
     */
    public void setSelfOut(StringBuilder buffer) {
        __buffer = buffer;
    }

    /**
     * Get the buffer out from this instance
     *
     * @return a <code>StringBuilder</code>
     */
    public StringBuilder getSelfOut() {
        return __buffer;
    }

    /**
     * Construct a root text builder
     */
    public TextBuilder() {
        __buffer = new StringBuilder();
        __caller = null;
    }

    /**
     * Construct a chained text builder with a {@link #caller()} instance
     *
     * @param caller
     */
    public TextBuilder(TextBuilder caller) {
        this.__caller = caller;
        __buffer = (null == caller) ? new StringBuilder() : null;
    }

    /**
     * A data structure used to store both character based content and it's
     * binary byte array. This is used to optimize the performance when Rythm
     * is used to output to a binary outputstream, where the
     */
    protected static final class StrBuf {
        private final String s_;
        private byte[] ba_;

        public StrBuf(String s, byte[] ba) {
            if (null == s || "".equals(s)) {
                s_ = "";
                ba_ = new byte[]{};
            } else {
                s_ = s;
                ba_ = ba;
            }
        }

        public StrBuf(String s) {
            if (null == s || "".equals(s)) {
                s_ = "";
                ba_ = new byte[]{};
            } else {
                s_ = s;
            }
        }

        public String toString() {
            return s_;
        }

        public byte[] toBinary() {
            if (null == ba_) {
                ba_ = s_.getBytes();
            }
            return ba_;
        }

        @Override
        public int hashCode() {
            return s_.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof StrBuf) {
                return s_.equals(((StrBuf) obj).s_);
            }
            return false;
        }
    }

    /**
     * Append a {@link StrBuf} content into the buffer
     *
     * @param wrapper
     */
    protected void __append(StrBuf wrapper) {
        __buffer.append(wrapper.toString());
    }

    private void p_(StrBuf wrapper) {
        if (null != __buffer) __append(wrapper);
        else __caller.p(wrapper);
    }

    /**
     * Print a {@link StrBuf} to internal buffer or output (os or writer)
     * <code>null</code> object will not be printed
     *
     * @param wrapper
     * @return this builder
     */
    public TextBuilder p(StrBuf wrapper) {
        if (null != wrapper) p_(wrapper);
        return this;
    }

    /**
     * Append a object to internal buffer or output (os or writer).
     *
     * @param o
     */
    protected void __append(Object o) {
        __buffer.append(o.toString());
    }

    private void p_(Object o) {
        if (null != __buffer) __append(o);
        else __caller.p(o);
    }

    /**
     * Print a {@link java.lang.Object} to internal buffer or output (os or writer)
     * <code>null</code> object will not be printed
     *
     * @param o
     * @return this builder
     */
    public final TextBuilder p(Object o) {
        if (null != o) p_(o);
        return this;
    }


    /**
     * Append a char to internal buffer
     *
     * @param c
     */
    protected void __append(char c) {
        __buffer.append(c);
    }

    /**
     * Print a char to internal buffer or output (os or writer)
     *
     * @param c
     * @return this builder
     */
    public final TextBuilder p(char c) {
        if (null != __buffer) __append(c);
        else __caller.p(c);
        return this;
    }


    /**
     * Append a byte to internal buffer
     *
     * @param b
     */
    protected void __append(byte b) {
        __buffer.append(b);
    }

    /**
     * Print a byte to internal buffer or output (os or writer)
     *
     * @param b
     * @return this builder
     */
    public final TextBuilder p(byte b) {
        if (null != __buffer) __append(b);
        else __caller.p(b);
        return this;
    }

    /**
     * Append an integer to internal buffer
     *
     * @param i
     */
    protected void __append(int i) {
        __buffer.append(i);
    }

    /**
     * Print an integer to internal buffer or output (os or writer)
     *
     * @param i
     * @return this builder
     */
    public final TextBuilder p(int i) {
        if (null != __buffer) __append(i);
        else __caller.p(i);
        return this;
    }

    /**
     * Append a long to internal buffer
     *
     * @param l
     */
    protected void __append(long l) {
        __buffer.append(l);
    }

    /**
     * Print a long to internal buffer or output (os or writer)
     *
     * @param l
     * @return this builder
     */
    public final TextBuilder p(long l) {
        if (null != __buffer) __append(l);
        else __caller.p(l);
        return this;
    }

    /**
     * Append a float to internal buffer
     *
     * @param f
     */
    protected void __append(float f) {
        __buffer.append(f);
    }

    /**
     * Print a float to internal buffer or output (os or writer)
     *
     * @param f
     * @return this builder
     */
    public final TextBuilder p(float f) {
        if (null != __buffer) __append(f);
        else __caller.p(f);
        return this;
    }

    /**
     * Append a double to internal buffer
     *
     * @param d
     */
    protected void __append(double d) {
        __buffer.append(d);
    }

    /**
     * Print a double to internal buffer or output (os or writer)
     *
     * @param d
     * @return this builder
     */
    public final TextBuilder p(double d) {
        if (null != __buffer) __append(d);
        else __caller.p(d);
        return this;
    }

    /**
     * Append a boolean to internal buffer
     *
     * @param b
     */
    protected void __append(boolean b) {
        __buffer.append(b);
    }

    /**
     * Print a boolean to internal buffer or output (os or writer)
     *
     * @param b
     * @return this builder
     */
    public final TextBuilder p(boolean b) {
        if (null != __buffer) __append(b);
        else __caller.p(b);
        return this;
    }

    /**
     * Print an object followed by an new line break
     *
     * @param o
     * @return this builder
     */
    public final TextBuilder pn(Object o) {
        if (null != o) p_(o);
        p_('\n');
        return this;
    }

    /**
     * Print an new line break
     *
     * @return this builder
     */
    public final TextBuilder pn() {
        p_('\n');
        return this;
    }

    /**
     * Print an new line break followed by an object
     *
     * @param o
     * @return this builder
     */
    public final TextBuilder np(Object o) {
        p_('\n');
        if (null != o) p_(o);
        return this;
    }

    /**
     * Print an object followed by a tab <code>\t</code>
     *
     * @param o
     * @return this builder
     */
    public TextBuilder pt(Object o) {
        p("\t").p(o);
        return this;
    }

    /**
     * Print an object followed by a tab <code>\t</code> and an new line break
     *
     * @param o
     * @return this builder
     */
    public TextBuilder ptn(Object o) {
        p("\t").p(o).pn();
        return this;
    }

    /**
     * Print an object followed by two tabs <code>\t</code>
     *
     * @param o
     * @return this builder
     */
    public TextBuilder p2t(Object o) {
        p("\t\t").p(o);
        return this;
    }

    /**
     * Print an object followed by 2 tabs <code>\t</code> and an line break
     *
     * @param o
     * @return this builder
     */
    public TextBuilder p2tn(Object o) {
        p("\t\t").p(o).pn();
        return this;
    }

    /**
     * Print an object followed by 3 tabs <code>\t</code>
     *
     * @param o
     * @return this builder
     */
    public TextBuilder p3t(Object o) {
        p("\t\t\t").p(o);
        return this;
    }

    /**
     * Print an object followed by 3 tabs <code>\t</code> and an line break
     *
     * @param o
     * @return this builder
     */
    public TextBuilder p3tn(Object o) {
        p("\t\t\t").p(o).pn();
        return this;
    }

    /**
     * Print an object followed by 4 tabs <code>\t</code>
     *
     * @param o
     * @return this builder
     */
    public TextBuilder p4t(Object o) {
        p("\t\t\t\t").p(o);
        return this;
    }

    /**
     * Print an object followed by 4 tabs <code>\t</code> and an line break
     *
     * @param o
     * @return this builder
     */
    public TextBuilder p4tn(Object o) {
        p("\t\t\t\t").p(o).pn();
        return this;
    }

    /**
     * Sub class could implement this method to append the generated
     * source code to the buffer
     *
     * @return this builder
     */
    public TextBuilder build() {
        return this;
    }

    /**
     * Return the internal buffer or caller's internal buffer if it is null
     *
     * @return the content
     */
    @Override
    public String toString() {
        return null != __buffer ? __buffer.toString() : __caller.toString();
    }

    /**
     * return a clone of this TextBuilder instance
     *
     * @param caller
     * @return the clone
     */
    public TextBuilder clone(TextBuilder caller) {
        try {
            TextBuilder tb = (TextBuilder) super.clone();
            tb.__caller = caller;
            return tb;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unexpected");
        }
    }
}
