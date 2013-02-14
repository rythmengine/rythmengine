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
package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.exception.FastRuntimeException;
import com.greenlaw110.rythm.template.ITemplate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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
     * @return
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
     * @return
     */
    protected ITemplate caller() {
        return (ITemplate) __caller;
    }

    /**
     * Alias of {@link #buffer()}
     *
     * @return
     */
    public StringBuilder getBuffer() {
        return buffer();
    }

    /**
     * Set the internal buffer to this instance or if the
     * {@link #caller()} exists, to the caller instance
     *
     * @param buffer
     */
    public void setBuffer(StringBuilder buffer) {
        if (null != __caller) ((TextBuilder) __caller).setBuffer(buffer);
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
     * @return
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
    protected void append(StrBuf wrapper) {
        __buffer.append(wrapper.toString());
    }

    private void p_(StrBuf wrapper) {
        if (null != __buffer) append(wrapper);
        else __caller.p(wrapper);
    }

    /**
     * Print a {@link StrBuf} to internal buffer or output (os or writer)
     * <code>null</code> object will not be printed
     *
     * @param wrapper
     * @return
     */
    public final TextBuilder p(StrBuf wrapper) {
        if (null != wrapper) p_(wrapper);
        return this;
    }

    /**
     * Append a object to internal buffer or output (os or writer).
     *
     * @param o
     */
    protected void append(Object o) {
        __buffer.append(o.toString());
    }

    private void p_(Object o) {
        if (null != __buffer) append(o);
        else __caller.p(o);
    }

    /**
     * Print a {@link java.lang.Object} to internal buffer or output (os or writer)
     * <code>null</code> object will not be printed
     *
     * @param o
     * @return
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
    protected void append(char c) {
        __buffer.append(c);
    }

    /**
     * Print a char to internal buffer or output (os or writer)
     *
     * @param c
     * @return
     */
    public final TextBuilder p(char c) {
        if (null != __buffer) append(c);
        else __caller.p(c);
        return this;
    }


    /**
     * Append a byte to internal buffer
     *
     * @param b
     */
    protected void append(byte b) {
        __buffer.append(b);
    }

    /**
     * Print a byte to internal buffer or output (os or writer)
     *
     * @param b
     * @return
     */
    public final TextBuilder p(byte b) {
        if (null != __buffer) append(b);
        else __caller.p(b);
        return this;
    }

    /**
     * Append an integer to internal buffer
     *
     * @param i
     */
    protected void append(int i) {
        __buffer.append(i);
    }

    /**
     * Print an integer to internal buffer or output (os or writer)
     *
     * @param i
     * @return
     */
    public final TextBuilder p(int i) {
        if (null != __buffer) append(i);
        else __caller.p(i);
        return this;
    }

    /**
     * Append a long to internal buffer
     *
     * @param l
     */
    protected void append(long l) {
        __buffer.append(l);
    }

    /**
     * Print a long to internal buffer or output (os or writer)
     *
     * @param l
     * @return
     */
    public final TextBuilder p(long l) {
        if (null != __buffer) append(l);
        else __caller.p(l);
        return this;
    }

    /**
     * Append a float to internal buffer
     *
     * @param f
     */
    protected void append(float f) {
        __buffer.append(f);
    }

    /**
     * Print a float to internal buffer or output (os or writer)
     *
     * @param f
     * @return
     */
    public final TextBuilder p(float f) {
        if (null != __buffer) append(f);
        else __caller.p(f);
        return this;
    }

    /**
     * Append a double to internal buffer
     *
     * @param d
     */
    protected void append(double d) {
        __buffer.append(d);
    }

    /**
     * Print a double to internal buffer or output (os or writer)
     *
     * @param d
     * @return
     */
    public final TextBuilder p(double d) {
        if (null != __buffer) append(d);
        else __caller.p(d);
        return this;
    }

    /**
     * Append a boolean to internal buffer
     *
     * @param b
     */
    protected void append(boolean b) {
        __buffer.append(b);
    }

    /**
     * Print a boolean to internal buffer or output (os or writer)
     *
     * @param b
     * @return
     */
    public final TextBuilder p(boolean b) {
        if (null != __buffer) append(b);
        else __caller.p(b);
        return this;
    }

    /**
     * Print an object followed by an new line break
     *
     * @param o
     * @return
     */
    public final TextBuilder pn(Object o) {
        if (null != o) p_(o);
        p_('\n');
        return this;
    }

    /**
     * Print an new line break
     *
     * @return
     */
    public final TextBuilder pn() {
        p_('\n');
        return this;
    }

    /**
     * Print an new line break followed by an object
     *
     * @param o
     * @return
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
     * @return
     */
    public TextBuilder pt(Object o) {
        p("\t").p(o);
        return this;
    }

    /**
     * Print an object followed by a tab <code>\t</code> and an new line break
     *
     * @param o
     * @return
     */
    public TextBuilder ptn(Object o) {
        p("\t").p(o).pn();
        return this;
    }

    /**
     * Print an object followed by two tabs <code>\t</code>
     *
     * @param o
     * @return
     */
    public TextBuilder p2t(Object o) {
        p("\t\t").p(o);
        return this;
    }

    /**
     * Print an object followed by 2 tabs <code>\t</code> and an line break
     *
     * @param o
     * @return
     */
    public TextBuilder p2tn(Object o) {
        p("\t\t").p(o).pn();
        return this;
    }

    /**
     * Print an object followed by 3 tabs <code>\t</code>
     *
     * @param o
     * @return
     */
    public TextBuilder p3t(Object o) {
        p("\t\t\t").p(o);
        return this;
    }

    /**
     * Print an object followed by 3 tabs <code>\t</code> and an line break
     *
     * @param o
     * @return
     */
    public TextBuilder p3tn(Object o) {
        p("\t\t\t").p(o).pn();
        return this;
    }

    /**
     * Print an object followed by 4 tabs <code>\t</code>
     *
     * @param o
     * @return
     */
    public TextBuilder p4t(Object o) {
        p("\t\t\t\t").p(o);
        return this;
    }

    /**
     * Print an object followed by 4 tabs <code>\t</code> and an line break
     *
     * @param o
     * @return
     */
    public TextBuilder p4tn(Object o) {
        p("\t\t\t\t").p(o).pn();
        return this;
    }

    /**
     * Sub class could implement this method to append the generated
     * source code to the buffer
     *
     * @return
     */
    public TextBuilder build() {
        return this;
    }

    /**
     * Return the internal buffer or caller's internal buffer if it is null
     *
     * @return
     */
    @Override
    public String toString() {
        return null != __buffer ? __buffer.toString() : __caller.toString();
    }

    /**
     * return a clone of this TextBuilder instance
     *
     * @param caller
     * @return
     */
    public TextBuilder clone(TextBuilder caller) {
        try {
            TextBuilder tb = (TextBuilder) super.clone();
            tb.__caller = caller;
            return tb;
        } catch (CloneNotSupportedException e) {
            throw new FastRuntimeException("Unexpected");
        }
    }

    public static void main(String[] args) throws Exception {

        StrBuf x = new StrBuf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n          \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n <head>\n  <title>Stock Prices - Rythm</title>\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n  <meta http-equiv=\"Content-Style-Type\" content=\"text/css\" />\n  <meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n  <link rel=\"shortcut icon\" href=\"/images/favicon.ico\" />\n  <link rel=\"stylesheet\" type=\"text/css\" href=\"/css/style.css\" media=\"all\" />\n  <script type=\"text/javascript\" src=\"/js/util.js\"></script>\n  <style type=\"text/css\">\n  /*<![CDATA[*/\n\nbody {\n    color: #333333;\n    line-height: 150%;\n}\n\nthead {\n    font-weight: bold;\n    background-color: #CCCCCC;\n}\n\n.odd {\n    background-color: #FFCCCC;\n}\n\n.even {\n    background-color: #CCCCFF;\n}\n\n.minus {\n    color: #FF0000;\n}\n\n  /*]]>*/\n  </style>\n\n </head>\n\n <body>\n\n  <h1>Stock Prices</h1>\n\n  <table>\n   <thead>\n    <tr>\n     <th>#</th><th>symbol</th><th>name</th><th>price</th><th>change</th><th>ratio</th>\n    </tr>\n   </thead>\n   <tbody>\n   \n", new byte[]{60, 63, 120, 109, 108, 32, 118, 101, 114, 115, 105, 111, 110, 61, 92, 34, 49, 46, 48, 92, 34, 32, 101, 110, 99, 111, 100, 105, 110, 103, 61, 92, 34, 85, 84, 70, 45, 56, 92, 34, 63, 62, 92, 110, 60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 32, 80, 85, 66, 76, 73, 67, 32, 92, 34, 45, 47, 47, 87, 51, 67, 47, 47, 68, 84, 68, 32, 88, 72, 84, 77, 76, 32, 49, 46, 48, 32, 84, 114, 97, 110, 115, 105, 116, 105, 111, 110, 97, 108, 47, 47, 69, 78, 92, 34, 92, 110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 92, 34, 104, 116, 116, 112, 58, 47, 47, 119, 119, 119, 46, 119, 51, 46, 111, 114, 103, 47, 84, 82, 47, 120, 104, 116, 109, 108, 49, 47, 68, 84, 68, 47, 120, 104, 116, 109, 108, 49, 45, 116, 114, 97, 110, 115, 105, 116, 105, 111, 110, 97, 108, 46, 100, 116, 100, 92, 34, 62, 92, 110, 60, 104, 116, 109, 108, 32, 120, 109, 108, 110, 115, 61, 92, 34, 104, 116, 116, 112, 58, 47, 47, 119, 119, 119, 46, 119, 51, 46, 111, 114, 103, 47, 49, 57, 57, 57, 47, 120, 104, 116, 109, 108, 92, 34, 32, 120, 109, 108, 58, 108, 97, 110, 103, 61, 92, 34, 101, 110, 92, 34, 32, 108, 97, 110, 103, 61, 92, 34, 101, 110, 92, 34, 62, 92, 110, 32, 60, 104, 101, 97, 100, 62, 92, 110, 32, 32, 60, 116, 105, 116, 108, 101, 62, 83, 116, 111, 99, 107, 32, 80, 114, 105, 99, 101, 115, 32, 45, 32, 82, 121, 116, 104, 109, 60, 47, 116, 105, 116, 108, 101, 62, 92, 110, 32, 32, 60, 109, 101, 116, 97, 32, 104, 116, 116, 112, 45, 101, 113, 117, 105, 118, 61, 92, 34, 67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101, 92, 34, 32, 99, 111, 110, 116, 101, 110, 116, 61, 92, 34, 116, 101, 120, 116, 47, 104, 116, 109, 108, 59, 32, 99, 104, 97, 114, 115, 101, 116, 61, 85, 84, 70, 45, 56, 92, 34, 32, 47, 62, 92, 110, 32, 32, 60, 109, 101, 116, 97, 32, 104, 116, 116, 112, 45, 101, 113, 117, 105, 118, 61, 92, 34, 67, 111, 110, 116, 101, 110, 116, 45, 83, 116, 121, 108, 101, 45, 84, 121, 112, 101, 92, 34, 32, 99, 111, 110, 116, 101, 110, 116, 61, 92, 34, 116, 101, 120, 116, 47, 99, 115, 115, 92, 34, 32, 47, 62, 92, 110, 32, 32, 60, 109, 101, 116, 97, 32, 104, 116, 116, 112, 45, 101, 113, 117, 105, 118, 61, 92, 34, 67, 111, 110, 116, 101, 110, 116, 45, 83, 99, 114, 105, 112, 116, 45, 84, 121, 112, 101, 92, 34, 32, 99, 111, 110, 116, 101, 110, 116, 61, 92, 34, 116, 101, 120, 116, 47, 106, 97, 118, 97, 115, 99, 114, 105, 112, 116, 92, 34, 32, 47, 62, 92, 110, 32, 32, 60, 108, 105, 110, 107, 32, 114, 101, 108, 61, 92, 34, 115, 104, 111, 114, 116, 99, 117, 116, 32, 105, 99, 111, 110, 92, 34, 32, 104, 114, 101, 102, 61, 92, 34, 47, 105, 109, 97, 103, 101, 115, 47, 102, 97, 118, 105, 99, 111, 110, 46, 105, 99, 111, 92, 34, 32, 47, 62, 92, 110, 32, 32, 60, 108, 105, 110, 107, 32, 114, 101, 108, 61, 92, 34, 115, 116, 121, 108, 101, 115, 104, 101, 101, 116, 92, 34, 32, 116, 121, 112, 101, 61, 92, 34, 116, 101, 120, 116, 47, 99, 115, 115, 92, 34, 32, 104, 114, 101, 102, 61, 92, 34, 47, 99, 115, 115, 47, 115, 116, 121, 108, 101, 46, 99, 115, 115, 92, 34, 32, 109, 101, 100, 105, 97, 61, 92, 34, 97, 108, 108, 92, 34, 32, 47, 62, 92, 110, 32, 32, 60, 115, 99, 114, 105, 112, 116, 32, 116, 121, 112, 101, 61, 92, 34, 116, 101, 120, 116, 47, 106, 97, 118, 97, 115, 99, 114, 105, 112, 116, 92, 34, 32, 115, 114, 99, 61, 92, 34, 47, 106, 115, 47, 117, 116, 105, 108, 46, 106, 115, 92, 34, 62, 60, 47, 115, 99, 114, 105, 112, 116, 62, 92, 110, 32, 32, 60, 115, 116, 121, 108, 101, 32, 116, 121, 112, 101, 61, 92, 34, 116, 101, 120, 116, 47, 99, 115, 115, 92, 34, 62, 92, 110, 32, 32, 47, 42, 60, 33, 91, 67, 68, 65, 84, 65, 91, 42, 47, 92, 110, 92, 110, 98, 111, 100, 121, 32, 123, 92, 110, 32, 32, 32, 32, 99, 111, 108, 111, 114, 58, 32, 35, 51, 51, 51, 51, 51, 51, 59, 92, 110, 32, 32, 32, 32, 108, 105, 110, 101, 45, 104, 101, 105, 103, 104, 116, 58, 32, 49, 53, 48, 37, 59, 92, 110, 125, 92, 110, 92, 110, 116, 104, 101, 97, 100, 32, 123, 92, 110, 32, 32, 32, 32, 102, 111, 110, 116, 45, 119, 101, 105, 103, 104, 116, 58, 32, 98, 111, 108, 100, 59, 92, 110, 32, 32, 32, 32, 98, 97, 99, 107, 103, 114, 111, 117, 110, 100, 45, 99, 111, 108, 111, 114, 58, 32, 35, 67, 67, 67, 67, 67, 67, 59, 92, 110, 125, 92, 110, 92, 110, 46, 111, 100, 100, 32, 123, 92, 110, 32, 32, 32, 32, 98, 97, 99, 107, 103, 114, 111, 117, 110, 100, 45, 99, 111, 108, 111, 114, 58, 32, 35, 70, 70, 67, 67, 67, 67, 59, 92, 110, 125, 92, 110, 92, 110, 46, 101, 118, 101, 110, 32, 123, 92, 110, 32, 32, 32, 32, 98, 97, 99, 107, 103, 114, 111, 117, 110, 100, 45, 99, 111, 108, 111, 114, 58, 32, 35, 67, 67, 67, 67, 70, 70, 59, 92, 110, 125, 92, 110, 92, 110, 46, 109, 105, 110, 117, 115, 32, 123, 92, 110, 32, 32, 32, 32, 99, 111, 108, 111, 114, 58, 32, 35, 70, 70, 48, 48, 48, 48, 59, 92, 110, 125, 92, 110, 92, 110, 32, 32, 47, 42, 93, 93, 62, 42, 47, 92, 110, 32, 32, 60, 47, 115, 116, 121, 108, 101, 62, 92, 110, 92, 110, 32, 60, 47, 104, 101, 97, 100, 62, 92, 110, 92, 110, 32, 60, 98, 111, 100, 121, 62, 92, 110, 92, 110, 32, 32, 60, 104, 49, 62, 83, 116, 111, 99, 107, 32, 80, 114, 105, 99, 101, 115, 60, 47, 104, 49, 62, 92, 110, 92, 110, 32, 32, 60, 116, 97, 98, 108, 101, 62, 92, 110, 32, 32, 32, 60, 116, 104, 101, 97, 100, 62, 92, 110, 32, 32, 32, 32, 60, 116, 114, 62, 92, 110, 32, 32, 32, 32, 32, 60, 116, 104, 62, 35, 60, 47, 116, 104, 62, 60, 116, 104, 62, 115, 121, 109, 98, 111, 108, 60, 47, 116, 104, 62, 60, 116, 104, 62, 110, 97, 109, 101, 60, 47, 116, 104, 62, 60, 116, 104, 62, 112, 114, 105, 99, 101, 60, 47, 116, 104, 62, 60, 116, 104, 62, 99, 104, 97, 110, 103, 101, 60, 47, 116, 104, 62, 60, 116, 104, 62, 114, 97, 116, 105, 111, 60, 47, 116, 104, 62, 92, 110, 32, 32, 32, 32, 60, 47, 116, 114, 62, 92, 110, 32, 32, 32, 60, 47, 116, 104, 101, 97, 100, 62, 92, 110, 32, 32, 32, 60, 116, 98, 111, 100, 121, 62, 92, 110, 32, 32, 32, 92, 110});// line:118

        OutputStream os = new ByteArrayOutputStream();
        os.write(x.toBinary());
        System.out.println(os.toString());
    }
}
