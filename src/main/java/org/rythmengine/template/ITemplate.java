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

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.utils.Escape;
import org.rythmengine.utils.JSONWrapper;

import java.io.OutputStream;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Define a template instance API
 */
public interface ITemplate extends ITag, Cloneable {

    /**
     * Return the engine instance that is running this template
     * 
     * @return the {@link org.rythmengine.RythmEngine engine} instance
     */
    RythmEngine __engine();

    /**
     * Return the template class of this template instance
     * 
     * @param useCaller if set to true then return caller template class if this template has no template class 
     * @return the template class
     */
    TemplateClass __getTemplateClass(boolean useCaller);

    /**
     * Set binary output stream to the template instance.
     *
     * @param os
     * @throws NullPointerException  if os specified is null
     * @throws IllegalStateException if output stream or {@link #__setWriter(java.io.Writer) writer}
     *                               is already set
     * @return this template instance
     */
    ITemplate __setOutputStream(OutputStream os);

    /**
     * Set a character based writer to the template instance
     *
     * @param writer
     * @throws NullPointerException  if os specified is null
     * @throws IllegalStateException if {@link #__setOutputStream(java.io.OutputStream) output stream}
     *                               or writer is already set
     * @return this template instance
     */
    ITemplate __setWriter(Writer writer);

    /**
     * Set user context to the template instance
     *   
     * @param userContext
     * @return this template instance
     */
    ITemplate __setUserContext(Map<String, Object> userContext);

    /**
     * Return user context previously set to this template instance. 
     * if there is no user context has been set, then an empty Map is returned.
     * 
     * @return the user context
     */
    Map<String, Object> __getUserContext();

    /**
     * Set renderArgs in name-value pair
     *
     * @param args
     * @return this template instance
     */
    ITemplate __setRenderArgs(Map<String, Object> args);

    /**
     * Set renderArgs in position
     *
     * @param args
     * @return this template instance
     */
    ITemplate __setRenderArgs(Object... args);

    /**
     * Set a render arg by name
     *
     * @param name
     * @param arg
     * @return this template instance
     */
    ITemplate __setRenderArg(String name, Object arg);

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
     * @return this template instance
     */
    ITemplate __setRenderArg(int position, Object arg);

    /**
     * Set renderArgs using JSON data
     *
     * @param jsonData
     * @return this template instance
     */
    ITemplate __setRenderArg(JSONWrapper jsonData);

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
     * Set secure code (for sandbox purpse)
     * 
     * @param secureCode
     * @return this template
     */
    ITemplate __setSecureCode(String secureCode);

    

    /**
     * Get a copy of this template instance and pass in the engine and caller
     *
     * @param engine the rythm engine
     * @param caller the caller template
     * @return a cloned instance of this template class
     */
    ITemplate __cloneMe(RythmEngine engine, ITemplate caller);

    /**
     * (not API)
     * Return the current locale
     * 
     * @return the locale
     */
    Locale __curLocale();

    /**
     * (not API)
     * Return the current escape scheme
     * 
     * @return the escape
     */
    Escape __curEscape();
    
    /**
     * (not API)
     * Return current code type.
     *
     * @return current {@link org.rythmengine.extension.ICodeType type}
     */
    ICodeType __curCodeType();

    /**
     * The render time context. Not to be used in user application or template
     */
    public static class __Context {

        /**
         * Code type stack. Used to enable the
         * {@link org.rythmengine.conf.RythmConfigurationKey#FEATURE_NATURAL_TEMPLATE_ENABLED}
         * 
         * @see {@link #localeStack}
         */
        private Deque<ICodeType> codeTypeStack = new ConcurrentLinkedDeque<ICodeType>();

        /**
         * template escape stack. Used to enable the
         * {@link org.rythmengine.conf.RythmConfigurationKey#FEATURE_SMART_ESCAPE_ENABLED}
         */
        private Deque<Escape> escapeStack = new ConcurrentLinkedDeque<Escape>();

        /**
         * template locale stack. Used to track the locale in the current context.
         */
        private Deque<Locale> localeStack = new ConcurrentLinkedDeque<Locale>();

        private TemplateBase tmpl;
        
        private RythmConfiguration conf;

        private void setTemplate(TemplateBase tmpl, RythmConfiguration conf) {
            this.tmpl = tmpl;
            this.conf = conf;
        }
        
        /**
         * init the context with template and base code type
         *
         * @param templateBase
         * @param type
         * @param locale
         */
        public void init(TemplateBase templateBase, ICodeType type, Locale locale, TemplateClass tc,  RythmEngine engine) {
            if (null == type) {
                type = engine.renderSettings.codeType();
                if (null == type) type = tc.codeType;
            }
            if (null == locale) {
                locale = engine.renderSettings.locale();
            }
            codeTypeStack.push(type);
            localeStack.push(locale);
            setTemplate(templateBase, engine.conf());
        }

        public ICodeType currentCodeType() {
            if (codeTypeStack.isEmpty()) {
                return conf.defaultCodeType();
            } else {
                return codeTypeStack.peek();
            }
        }

        public void pushCodeType(ICodeType type) {
            ICodeType cur = currentCodeType();
            if (null != cur) {
                type.setParent(cur);
            }
            codeTypeStack.push(type);
            Rythm.RenderTime.setCodeType(type);
        }

        public ICodeType popCodeType() {
            ICodeType cur = codeTypeStack.pop();
            cur.setParent(null);
            return cur;
        }
        
        public Locale currentLocale() {
            if (localeStack.isEmpty()) {
                return conf.locale();
            } else {
                return localeStack.peek();
            }
        }
        
        public void pushLocale(Locale locale) {
            localeStack.push(locale);
        }
        
        public Locale popLocale() {
            return localeStack.pop();
        }

        public Escape currentEscape() {
            if (!escapeStack.isEmpty()) {
                return escapeStack.peek();
            } else {
                return currentCodeType().escape();
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
            //codeTypeStack = new Stack<ICodeType>();
            //escapeStack = new Stack<Escape>();
            //localeStack = new Stack<Locale>();
//            codeTypeStack.addAll(clone.codeTypeStack);
//            escapeStack.addAll(clone.escapeStack);
//            localeStack.addAll(clone.localeStack);
//            setTemplate(tmpl, conf);
        }
    }

}
