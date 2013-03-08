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

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.utils.Escape;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.utils.JSONWrapper;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Stack;

/**
 * Define a template instance API
 */
public interface ITemplate extends Cloneable {

    /**
     * Set binary output stream to the template instance.
     *
     * @param os
     * @throws NullPointerException  if os specified is null
     * @throws IllegalStateException if output stream or {@link #__setWriter(java.io.Writer) writer}
     *                               is already set
     */
    void __setOutputStream(OutputStream os);

    /**
     * Set a character based writer to the template instance
     *
     * @param writer
     * @throws NullPointerException  if os specified is null
     * @throws IllegalStateException if {@link #__setOutputStream(java.io.OutputStream) output stream}
     *                               or writer is already set
     */
    void __setWriter(Writer writer);

    /**
     * Set renderArgs in name-value pair
     *
     * @param args
     */
    void __setRenderArgs(Map<String, Object> args);

    /**
     * Set renderArgs in position
     *
     * @param args
     */
    void __setRenderArgs(Object... args);

    /**
     * Set a render arg by name
     *
     * @param name
     * @param arg
     */
    void __setRenderArg(String name, Object arg);

    /**
     * Return a render arg value by name
     *
     * @param name
     * @param <T>
     * @return render arg by name
     */
    <T> T __getRenderArg(String name);

    /**
     * Set a render arg by position
     *
     * @param position
     * @param arg
     */
    void __setRenderArg(int position, Object arg);

    /**
     * Set renderArgs using JSON data
     *
     * @param jsonData
     */
    void __setRenderArg(JSONWrapper jsonData);

    /**
     * Render the template and return result as String
     *
     * @return render result
     */
    String render();

    /**
     * Render the template and put the result into outputstream
     *
     * @param os
     */
    void render(OutputStream os);

    /**
     * Render the template and put the result into writer
     *
     * @param w
     */
    void render(Writer w);

    /**
     * Must be called before real render() happened.
     * Also if the template extends a parent template, then
     * the parent template's __init() must be called before this template's __init()
     */
    void __init();

    /**
     * Return the internal buffer
     *
     * @return buffer
     */
    StringBuilder __getBuffer();

    /**
     * Set the internal buffer
     *
     * @param sb
     */
    void __setBuffer(StringBuilder sb);

    /**
     * Get a copy of this template instance and pass in the engine and caller
     *
     * @param engine the rythm engine
     * @param caller the caller template
     * @return a cloned instance of this template class
     */
    ITemplate __cloneMe(RythmEngine engine, ITemplate caller);

    /**
     * The render time context. Not to be used in user application or template
     */
    public static class __Context {

        /**
         * template lang stack. Used to enable the
         * {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#FEATURE_NATURAL_TEMPLATE_ENABLED}
         */
        public Stack<ILang> langStack = new Stack<ILang>();

        /**
         * template escape stack. Used to enable the
         * {@link com.greenlaw110.rythm.conf.RythmConfigurationKey#FEATURE_SMART_ESCAPE_ENABLED}
         */
        public Stack<Escape> escapeStack = new Stack<Escape>();

        /**
         * init the context with template and base lang
         *
         * @param templateBase
         * @param lang
         */
        public void init(TemplateBase templateBase, ILang lang) {
            if (null == lang) {
                TemplateClass tc = templateBase.__getTemplateClass(true);
                lang = ILang.DefImpl.probeFileName(tc.name(), templateBase.__engine().conf().defaultLang());
            }
            langStack.push(lang);
        }

        public ILang currentLang() {
            if (langStack.isEmpty()) {
                return null;
            } else {
                return langStack.peek();
            }
        }

        public void pushLang(ILang lang) {
            ILang cur = currentLang();
            if (null != cur) {
                lang.setParent(cur);
            }
            langStack.push(lang);
            Rythm.RenderTime.setLang(lang);
        }

        public ILang popLang() {
            ILang cur = langStack.pop();
            cur.setParent(null);
            return cur;
        }

        public Escape currentEscape() {
            if (!escapeStack.isEmpty()) {
                return escapeStack.peek();
            } else {
                return currentLang().escape();
            }
        }

        public void pushEscape(Escape escape) {
            escapeStack.push(escape);
            Rythm.RenderTime.setEscape(escape);
        }

        public Escape popEscape() {
            return escapeStack.pop();
        }

        public __Context() {
            langStack = new Stack<ILang>();
            escapeStack = new Stack<Escape>();
        }

        public __Context(__Context clone) {
            langStack = new Stack<ILang>();
            escapeStack = new Stack<Escape>();
            langStack.addAll(clone.langStack);
            escapeStack.addAll(clone.escapeStack);
        }
    }

}
