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

import com.alibaba.fastjson.JSON;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfiguration;
import com.greenlaw110.rythm.exception.FastRuntimeException;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.extension.II18nMessageResolver;
import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.internal.IEvent;
import com.greenlaw110.rythm.internal.RythmEvents;
import com.greenlaw110.rythm.internal.TemplateBuilder;
import com.greenlaw110.rythm.internal.compiler.ClassReloadException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.*;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The base class of template implementation. It provides a set of
 * protected methods which is handy to use in template authoring
 */
public abstract class TemplateBase extends TemplateBuilder implements ITemplate {

    /**
     * The logger
     */
    protected static final ILogger __logger = Logger.get(TemplateBase.class);

    /**
     * The rythm engine that run this template
     */
    protected transient RythmEngine __engine = null;

    /**
     * The template class
     */
    private transient TemplateClass __templateClass = null;

    /**
     * Set template class and template lang to this template instance
     * <p/>
     * <p>Not to be called in user application or template</p>
     *
     * @param templateClass
     * @param lang
     */
    public void __setTemplateClass(TemplateClass templateClass, ILang lang, Locale locale) {
        this.__templateClass = templateClass;
        __ctx.init(this, lang, locale);
        if (null != __parent) {
            __parent.__ctx.init(__parent, lang, locale);
        }
    }

    /**
     * Return {@link com.greenlaw110.rythm.utils.S#INSTANCE String helper instance}. Could be used in
     * template authoring. For example:
     * <p/>
     * <pre><code>
     * {@literal @}if (s().empty(userRight)){
     *    {@literal @}return
     * }
     * </code></pre>
     */
    protected S s() {
        return S.INSTANCE;
    }

    private Writer w;
    private OutputStream os;

    public void __setWriter(Writer writer) {
        if (null == writer) throw new NullPointerException();
        if (null != os) throw new IllegalStateException("Cannot set writer to template when outputstream is presented");
        if (null != this.w)
            throw new IllegalStateException("Cannot set writer to template when an writer is presented");
        this.w = writer;
    }

    public void __setOutputStream(OutputStream os) {
        if (null == os) throw new NullPointerException();
        if (null != w) throw new IllegalStateException("Cannot set output stream to template when writer is presented");
        if (null != this.os)
            throw new IllegalStateException("Cannot set output stream to template when an outputstream is presented");
        this.os = os;
    }

    /**
     * Stores render args of this template. The generated template source code
     * will also declare render args as separate protected field while keeping
     * a copy inside this Map data structure
     */
    protected Map<String, Object> __renderArgs = new HashMap<String, Object>();

    /**
     * Return the {@link RythmEngine engine} running this template
     *
     * @return the engine running the template
     */
    public RythmEngine __engine() {
        return null == __engine ? Rythm.engine() : __engine;
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     */
    protected void __invokeTag(int line, String name) {
        __engine.invokeTag(line, name, this, null, null, null);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param ignoreNonExistsTag
     */
    protected void __invokeTag(int line, String name, boolean ignoreNonExistsTag) {
        __engine.invokeTag(line, name, this, null, null, null, ignoreNonExistsTag);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params) {
        __engine.invokeTag(line, name, this, params, null, null);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     * @param ignoreNonExistsTag
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params, boolean ignoreNonExistsTag) {
        __engine.invokeTag(line, name, this, params, null, null, ignoreNonExistsTag);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     * @param body
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params, ITag.__Body body) {
        __engine.invokeTag(line, name, this, params, body, null);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     * @param body
     * @param ignoreNoExistsTag
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params, ITag.__Body body, boolean ignoreNoExistsTag) {
        __engine.invokeTag(line, name, this, params, body, null, ignoreNoExistsTag);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     * @param body
     * @param context
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params, ITag.__Body body, ITag.__Body context) {
        __engine.invokeTag(line, name, this, params, body, context);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     * @param body
     * @param context
     * @param ignoreNonExistsTag
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params, ITag.__Body body, ITag.__Body context, boolean ignoreNonExistsTag) {
        __engine.invokeTag(line, name, this, params, body, context, ignoreNonExistsTag);
    }

    /* to be used by dynamic generated sub classes */
    private String layoutContent = "";
    private Map<String, String> layoutSections = new HashMap<String, String>();
    private Map<String, Object> renderProperties = new HashMap<String, Object>();

    /**
     * The parent template (layout template)
     */
    protected TemplateBase __parent = null;

    /**
     * Construct a template instance
     */
    public TemplateBase() {
        super();
        Class<? extends TemplateBase> c = getClass();
        Class<?> pc = c.getSuperclass();
        if (TemplateBase.class.isAssignableFrom(pc) && !Modifier.isAbstract(pc.getModifiers())) {
            try {
                TemplateClass ptc = __engine().classes().getByClassName(pc.getName());
                if (null != ptc) {
                    __parent = (TemplateBase) ptc.asTemplate();
                } else {
                    __parent = (TemplateBase) pc.newInstance();
                }
                //__parent.__setTemplateClass(__engine().classes.getByClassName(pc.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Render another template from this template. Could be used in template authoring.
     * For example:
     * <p/>
     * <pre><code>
     * {@literal @}args String customTemplate, Map<String, Object> customParams
     * {@literal @}{ Object renderResult = render(customTemplate, customParams);
     * }
     * <p class="customer_content">@renderResult</p>
     * </code></pre>
     *
     * @param template
     * @param args
     * @return render result
     */
    protected RawData __render(String template, Object... args) {
        if (null == template) return new RawData("");
        return S.raw(__engine.sandbox().render(template, args));
    }

    /**
     * Render another template from within this template. Using the renderArgs
     * of this template.
     *
     * @param template
     * @return render result as {@link com.greenlaw110.rythm.utils.RawData}
     * @see #__render(String, Object...)
     */
    protected RawData __render(String template) {
        if (null == template) return new RawData("");
        return S.raw(__engine.sandbox().render(template, __renderArgs));
    }

    /**
     * Set layout content. Should not be used in user application or template
     *
     * @param body
     */
    protected final void __setLayoutContent(String body) {
        layoutContent = body;
    }

    /**
     * Add layout section. Should not be used in user application or template
     *
     * @param name
     * @param section
     */
    private void __addLayoutSection(String name, String section) {
        if (layoutSections.containsKey(name)) return;
        layoutSections.put(name, section);
    }

    private StringBuilder tmpOut = null;
    private String section = null;
    private TextBuilder tmpCaller = null;

    /**
     * Start a layout section. Not to be used in user application or template
     *
     * @param name
     */
    protected void __startSection(String name) {
        if (null == name) throw new NullPointerException("section name cannot be null");
        if (null != tmpOut) throw new IllegalStateException("section cannot be nested");
        tmpCaller = __caller;
        __caller = null;
        tmpOut = __buffer;
        __buffer = new StringBuilder();
        section = name;
    }

    /**
     * End a layout section. Not to be used in user application or template
     */
    protected void __endSection() {
        __endSection(false);
    }

    /**
     * End a layout section with a boolean flag mark if it is a default content or not.
     * Not to be used in user application or template
     *
     * @param def
     */
    protected void __endSection(boolean def) {
        if (null == tmpOut && null == tmpCaller) throw new IllegalStateException("section has not been started");
        __addLayoutSection(section, __buffer.toString());
        __buffer = tmpOut;
        __caller = tmpCaller;
        tmpOut = null;
        tmpCaller = null;
    }

    /**
     * Print a layout section by name. Not to be used in user application or template
     *
     * @param name
     */
    protected void __pLayoutSection(String name) {
        p(layoutSections.get(name));
    }

    /**
     * Get a section content as {@link com.greenlaw110.rythm.utils.RawData} by name. Not to be used in user application or template
     *
     * @param name
     * @return section data by name
     */
    protected RawData __getSection(String name) {
        return S.raw(layoutSections.get(name));
    }

    /**
     * Get layout content as {@link com.greenlaw110.rythm.utils.RawData}. Not to be used in user application or template
     *
     * @return layout content
     */
    protected RawData __getSection() {
        return S.raw(S.isEmpty(layoutContent) ? layoutSections.get("__CONTENT__") : layoutContent);
    }

    /**
     * Print the layout content. Not to be used in user application or template
     */
    protected void __pLayoutContent() {
        p(__getSection());
    }

    private void addAllLayoutSections(Map<String, String> sections) {
        if (null != sections) layoutSections.putAll(sections);
    }

    private void addAllRenderProperties(Map<String, Object> properties) {
        if (null != properties) renderProperties.putAll(properties);
    }

    /**
     * Not to be used in user application or template
     *
     * @return a <code>TemplateBase</code>
     */
    protected TemplateBase __internalClone() {
        try {
            return (TemplateBase) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Not to be used in user application or template
     *
     * @param engine the rythm engine
     * @param caller the caller template
     * @return cloned template
     */
    @Override
    public ITemplate __cloneMe(RythmEngine engine, ITemplate caller) {
        if (null == engine) throw new NullPointerException();
        TemplateBase tmpl = __internalClone();
        if (tmpl.__parent != null) {
            tmpl.__parent = (TemplateBase) tmpl.__parent.__cloneMe(engine, caller);
        }
        tmpl.__engine = engine;
        tmpl.__templateClass = __templateClass;
        tmpl.__ctx = new __Context(__ctx);
        //if (null != buffer) tmpl.__buffer = buffer;
        if (null != __buffer) tmpl.__buffer = new StringBuilder();
        tmpl.__renderArgs = new HashMap<String, Object>(__renderArgs.size());
        tmpl.layoutContent = "";
        tmpl.layoutSections = new HashMap<String, String>();
        tmpl.renderProperties = new HashMap<String, Object>();
        tmpl.section = null;
        tmpl.tmpCaller = null;
        tmpl.tmpOut = null;
        tmpl.__logTime = __logTime;
        tmpl.w = null;
        tmpl.os = null;
        if (null != caller) {
            tmpl.__caller = (TextBuilder) caller;
            tmpl.__setRenderArgs(((TemplateBase)caller).__renderArgs);
        }
        return tmpl;
    }

    /**
     * Not to be used in user application or template
     */
    protected void __internalInit() {
        __loadExtendingArgs();
        __init();
    }

    /**
     * the implementation of this method is to be generated by {@link com.greenlaw110.rythm.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    protected void __setup() {
    }

    /**
     * the implementation of this method is to be generated by {@link com.greenlaw110.rythm.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    protected void __loadExtendingArgs() {
    }

    /**
     * the implementation of this method is to be generated by {@link com.greenlaw110.rythm.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    @Override
    public void __init() {
    }

    private boolean __logTime() {
        return __logger.isDebugEnabled() && (__logTime || __engine.conf().logRenderTime());
    }

    /**
     * Get the template class of this template. Not to be used in user application or template
     *
     * @param useCaller
     * @return a <code>TemplateClass</code>
     */
    public TemplateClass __getTemplateClass(boolean useCaller) {
        TemplateClass tc = __templateClass;
        if (useCaller && null == tc) {
            TemplateBase caller = __caller();
            if (null != caller) return caller.__getTemplateClass(true);
        }
        return tc;
    }

    /**
     * Render to binary output stream. This method is usually called from API defined in
     * {@link RythmEngine}
     *
     * @param os
     */
    @Override
    public final void render(OutputStream os) {
        __setOutputStream(os);
        render();
    }

    /**
     * Render to character based writer. This method is usually called from API defined in
     * {@link RythmEngine}
     *
     * @param w
     */
    @Override
    public final void render(Writer w) {
        __setWriter(w);
        render();
    }

    /**
     * Trigger render events.
     * <p>Not an API for user application</p>
     * @param event
     * @param engine
     */
    protected void __triggerRenderEvent(IEvent<Void, ITemplate> event, RythmEngine engine) {
        event.trigger(engine, this);
    }

    /**
     * Render and return result in String. This method is usually called from API defined in
     * {@link RythmEngine}
     */
    @Override
    public final String render() {
        RythmEngine engine = __engine();
        RythmEngine.set(engine);
        try {
            long l = 0l;
            if (__logTime()) {
                l = System.currentTimeMillis();
            }

            __triggerRenderEvent(RythmEvents.ON_RENDER, engine);
            __setup();
            if (__logTime()) {
                __logger.debug("< preprocess [%s]: %sms", getClass().getName(), System.currentTimeMillis() - l);
                l = System.currentTimeMillis();
            }
            String s = __internalRender();
            __triggerRenderEvent(RythmEvents.RENDERED, engine);
            if (__logTime()) {
                __logger.debug("<<<<<<<<<<<< [%s] total render: %sms", getClass().getName(), System.currentTimeMillis() - l);
            }
            return s;
        } catch (ClassReloadException e) {
            if (__logger.isDebugEnabled()) {
                __logger.debug("Cannot hotswap class, try to restart engine...");
            }
            engine.restart(e);
            return render();
        } 
//        catch (ClassCastException e) {
//            if (__logger.isDebugEnabled()) {
//                __logger.debug("ClassCastException found, force refresh template and try again...");
//            }
//            engine.restart(e);
//            TemplateClass tc = engine.classes().getByClassName(getClass().getName());
//            tc.refresh(true);
//            ITemplate t = tc.asTemplate(__ctx.currentLang());
//            t.__setRenderArgs(__renderArgs);
//            return t.render();
//        }
    }

    private Writer w_ = null;

    /**
     * Set output file path
     *
     * @param path
     */
    protected void __setOutput(String path) {
        try {
            w_ = new BufferedWriter(new FileWriter(path));
        } catch (Exception e) {
            throw new FastRuntimeException(e.getMessage());
        }
    }

    /**
     * Set output file
     *
     * @param file
     */
    protected void __setOutput(File file) {
        try {
            w_ = new BufferedWriter(new FileWriter(file));
        } catch (Exception e) {
            throw new FastRuntimeException(e.getMessage());
        }
    }

    /**
     * Set output stream
     *
     * @param os
     */
    protected void __setOutput(OutputStream os) {
        w_ = new OutputStreamWriter(os);
    }

    /**
     * Set output writer
     *
     * @param w
     */
    protected void __setOutput(Writer w) {
        w_ = w;
    }

    private static final ThreadLocal<Boolean> cce_ = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    /**
     * Not to be used in user application or template
     */
    protected void __internalBuild() {
        w_ = null; // reset output destination
        RythmEngine engine = __engine();
        //if (!(engine.recordTemplateSourceOnError || engine.recordJavaSourceOnError)) {
        if (false) {
            __internalInit();
            build();
        } else {
//            try {
                try {
                    long l = 0l;
                    if (__logTime()) {
                        l = System.currentTimeMillis();
                    }
                    __internalInit();
                    build();
                    if (__logTime()) {
                        __logger.debug("<<<<<<<<<<<< [%s] build: %sms", getClass().getName(), System.currentTimeMillis() - l);
                    }
                } catch (RythmException e) {
                    throw e;
                } catch (Throwable e) {
//                    if (engine.isDevMode() && e instanceof ClassCastException) {
//                        // give one time retry for CCE
//                        boolean cce = cce_.get();
//                        if (!cce) {
//                            cce_.set(true);
//                            throw (ClassCastException)e;
//                        } else {
//                            cce_.set(false);
//                        }
//                    }
                    StackTraceElement[] stackTrace = e.getStackTrace();
                    String msg = null;
                    for (StackTraceElement se : stackTrace) {
                        String cName = se.getClassName();
                        if (cName.contains(TemplateClass.CN_SUFFIX)) {
                            // is it the embedded class?
                            if (cName.indexOf("$") != -1) {
                                cName = cName.substring(0, cName.lastIndexOf("$"));
                            }
                            TemplateClass tc = __engine.classes().getByClassName(cName);
                            if (null == tc) {
                                continue;
                            }
                            if (null == msg) {
                                msg = e.getMessage();
                                if (S.isEmpty(msg)) {
                                    msg = "Rythm runtime exception caused by " + e.getClass().getName();
                                }
                            }
                            RythmException re = new RythmException(__engine, e, tc, se.getLineNumber(), -1, msg);
                            int lineNo = re.templateLineNumber;
                            String key = tc.getKey().toString();
                            int i = key.indexOf('\n');
                            if (i == -1) i = key.indexOf('\r');
                            if (i > -1) {
                                key = key.substring(0, i - 1) + "...";
                            }
                            if (key.length() > 80) key = key.substring(0, 80) + "...";
                            if (lineNo != -1) {
                                StackTraceElement[] newStack = new StackTraceElement[stackTrace.length + 1];
                                newStack[0] = new StackTraceElement(tc.name(), "", key, lineNo);
                                System.arraycopy(stackTrace, 0, newStack, 1, stackTrace.length);
                                re.setStackTrace(newStack);
                            }
                            throw re;
                        }
                    }
                    throw (e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e));
                }
//            }
//            } catch (RuntimeException e) {
//                // try to restart engine
//                if (!Rythm.insideSandbox()) {
//                    try {
//                        __engine.restart(e);
//                    } catch (RuntimeException e0) {
//                        // ignore it because we already thrown it out
//                    }
//                }
//                throw e;
//            }
        }
        if (null != w_) {
            try {
                IO.writeContent(toString(), w_);
                w_ = null;
            } catch (Exception e) {
                Logger.error(e, "failed to write template content to output destination");
            }
        }
    }

    /**
     * Not to be used in user application or template
     */
    protected String __internalRender() {
        __internalBuild();
        if (null != __parent && __parent != this) {
            __parent.__setLayoutContent(toString());
            __parent.addAllLayoutSections(layoutSections);
            __parent.addAllRenderProperties(renderProperties);
            __parent.__setRenderArgs(__renderArgs);
            //__parent.__renderArgs.putAll(__renderArgs);
            return __parent.render();
        } else {
            return toString();
        }
    }

    /**
     * The {@link com.greenlaw110.rythm.internal.CodeBuilder} will generate the
     * implementation of this method usually
     *
     * @return this template as a {@link com.greenlaw110.rythm.utils.TextBuilder}
     */
    @Override
    public TextBuilder build() {
        return this;
    }

    /**
     * Return render arg type in array. Not to be used in user application or template
     */
    protected Class[] __renderArgTypeArray() {
        return null;
    }

    /**
     * Return render arg name by position
     * @param i
     * @return render argument name by position
     */
    protected String __renderArgName(int i) {
        return null;
    }

    /**
     * Return render arg type in Map. Not to be used in user application or template
     *
     * @return render args types mapped by name
     */
    protected Map<String, Class> __renderArgTypeMap() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public void __setRenderArgs(Map<String, Object> args) {
        __renderArgs.putAll(args);
    }

    @Override
    public void __setRenderArg(JSONWrapper jsonData) {
        if (jsonData.isArray()) {
            setJSONArray(jsonData.getArray());
        } else {
            setJSONObject(jsonData.getObject());
        }
    }

    private void setJSONArray(List<Object> jsonArray) {
        Class[] types = __renderArgTypeArray();
        int paraNo = jsonArray.size();
        if (types.length == 1 && types[0].equals(List.class)) {
            Map<String, Class> typeMap = __renderArgTypeMap();
            String vn = __renderArgName(0);
            Class c = typeMap.get(vn + "__0");
            Object p = JSON.parseArray(jsonArray.toString(), c);
            __setRenderArg(vn, p);
        } else {
            for (int i = 0; i < types.length; ++i) {
                if (i >= paraNo) break;
                Object o = jsonArray.get(i);
                Class c = types[i];
                Object p;
                if (o instanceof List) {
                    p = JSON.parseArray(o.toString(), c);
                } else {
                    p = JSON.parseObject(o.toString(), c);
                }
                __setRenderArg(i, p);
            }
        }
    }

    private void setJSONObject(Map<String, Object> jsonObject) {
        Map<String, Class> types = __renderArgTypeMap();
        for (String nm : jsonObject.keySet()) {
            if (types.containsKey(nm)) {
                Class c = types.get(nm);
                Object o = jsonObject.get(nm);
                Object p;
                if (o instanceof List) {
                    Map<String, Class> typeMap = __renderArgTypeMap();
                    String vn = __renderArgName(0);
                    Class c0 = typeMap.get(vn + "__0");
                    p = JSON.parseArray(o.toString(), c0);
                } else {
                    p = JSON.parseObject(o.toString(), c);
                }
                __setRenderArg(nm, p);
            }
        }
    }

    /**
     * Set render arg from {@link com.greenlaw110.rythm.template.ITag.__ParameterList tag params}
     * Not to be used in user application or template
     *
     * @param params
     */
    protected void __setRenderArgs0(ITag.__ParameterList params) {
        for (int i = 0; i < params.size(); ++i) {
            ITag.__Parameter param = params.get(i);
            if (null != param.name) __setRenderArg(param.name, param.value);
            else __setRenderArg(i, param.value);
        }
    }

    @Override
    public void __setRenderArgs(Object... args) {
    }

    @Override
    public void __setRenderArg(String name, Object arg) {
        __renderArgs.put(name, arg);
    }

    /**
     * alias of {@link #__setRenderArg(String, Object)}
     *
     * @param name
     * @param arg
     */
    protected final void __set(String name, Object arg) {
        __setRenderArg(name, arg);
    }

    /**
     * Return caller of the template when this template is
     * invoked as a {@link ITag tag}
     *
     * @return caller template
     */
    protected final TemplateBase __caller() {
        return null == __caller ? null : (TemplateBase) __caller;
    }

    @Override
    public <T> T __getRenderArg(String name) {
        Object val = __renderArgs.get(name);
        return (T) (null != val ? val : (null != __caller ? caller().__getRenderArg(name) : null));
    }

    /**
     * Alias of {@link #__getRenderArg(String)}
     *
     * @param name
     * @param <T>
     * @return a render argument
     */
    protected final <T> T __get(String name) {
        return __getRenderArg(name);
    }

    /**
     * Get render arg and do type cast to the class specified
     *
     * @param name
     * @param c
     * @param <T>
     * @return a render argument
     */
    protected final <T> T __getAs(String name, Class<T> c) {
        Object o = __getRenderArg(name);
        if (null == o) return null;
        return (T) o;
    }

    /**
     * Get render property by name. And do type case by the left value of the expression.
     * <p/>
     * <p>If no property found by name then return the default value specified</p>
     *
     * @param name
     * @param def
     * @param <T>
     * @return a render property
     */
    protected final <T> T __getRenderProperty(String name, T def) {
        Object o = renderProperties.get(name);
        return (T) (null == o ? def : o);
    }

    /**
     * Get render property by name. This is usually called by <code>@get()</code> directive in teh layout template.
     *
     * @param name
     * @param <T>
     * @return a render property
     * @see #__setRenderProperty(String, Object)
     */
    protected final <T> T __getRenderProperty(String name) {
        return (T) __getRenderProperty(name, null);
    }

    /**
     * Get render property by name and do type cast to the specified default value.
     * If the render property cannot be found by name, then return the default value
     *
     * @param name
     * @param def
     * @param <T>
     * @return a render property
     * @see #__getRenderProperty(String)
     */
    protected final <T> T __getRenderPropertyAs(String name, T def) {
        Object o = __getRenderProperty(name, def);
        return null == o ? def : (T) o;
    }

    /**
     * Set render property by name. This is pass value from sub (content) template
     * to parent (layout) template Usually this is invoked by <code>@set()</code>
     * directive in the sub template
     *
     * @param name
     * @param val
     * @see #__getRenderProperty(String)
     * @see #__getRenderProperty(String, Object)
     */
    protected final void __setRenderProperty(String name, Object val) {
        renderProperties.put(name, val);
    }

    /**
     * Handle template execution exception. Not to be called in user application or template
     *
     * @param e
     */
    protected final void __handleTemplateExecutionException(Exception e) {
        try {
            if (!RythmEvents.ON_RENDER_EXCEPTION.trigger(__engine(), F.T2(this, e))) {
                throw e;
            }
        } catch (RuntimeException e0) {
            throw e0;
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public void __setRenderArg(int position, Object arg) {
    }

    /**
     * The render context
     */
    protected __Context __ctx = new __Context();

    /**
     * Return current template lang. Not to be used in user application or template
     *
     * @return current {@link com.greenlaw110.rythm.extension.ILang lang}
     */
    public ILang __curLang() {
        return __ctx.currentLang();
    }
    
    public Locale __curLocale() {
        return __ctx.currentLocale();
    }

    private boolean appendToBuffer() {
        return null != __parent || (null == w && null == os);
    }

    private boolean appendToWriter() {
        return (null == __parent && null != w);
    }

    private boolean appendToOutputStream() {
        return (null == __parent && null != os);
    }

    @Override
    protected void __append(StrBuf wrapper) {
        if (appendToBuffer()) {
            super.__append(wrapper);
            return;
        }

        if (appendToOutputStream()) {
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(wrapper.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(Object o) {
        if (appendToBuffer()) super.__append(o);

        StrBuf wrapper = new StrBuf(o.toString());
        if (appendToOutputStream()) {
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(wrapper.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(char c) {
        if (appendToBuffer()) super.__append(c);

        if (appendToOutputStream()) {
            try {
                os.write(c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(int i) {
        if (appendToBuffer()) super.__append(i);

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(i));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(String.valueOf(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(long l) {
        if (appendToBuffer()) super.__append(l);

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(l));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(String.valueOf(l));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(float f) {
        if (appendToBuffer()) super.__append(f);

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(f));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(String.valueOf(f));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(double d) {
        if (appendToBuffer()) super.__append(d);

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(d));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(String.valueOf(d));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(boolean b) {
        if (appendToBuffer()) super.__append(b);

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(b));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (appendToWriter()) {
            try {
                w.write(String.valueOf(b));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ---- overwrite TemplateBuilder methods

    /*
     * make it public because ITag.Body will need it
     */
    @Override
    public Escape __defaultEscape() {
        return __ctx.currentEscape();
    }

    @Override
    public final TemplateBase pe(Object o) {
        return (TemplateBase) super.pe(o);
    }

    @Override
    public final TemplateBase pe(Object o, Escape escape) {
        return (TemplateBase) super.pe(o, escape);
    }

    // --- debugging interface
    protected static void __log(String msg, Object... args) {
        __logger.info(msg, args);
    }

    protected static void __debug(String msg, Object... args) {
        __logger.debug(msg, args);
    }

    protected static void __info(String msg, Object... args) {
        __logger.info(msg, args);
    }

    protected static void __warn(String msg, Object... args) {
        __logger.error(msg, args);
    }

    protected static void __warn(Throwable t, String msg, Object... args) {
        __logger.error(t, msg, args);
    }

    protected static void __error(String msg, Object... args) {
        __logger.error(msg, args);
    }

    protected static void __error(Throwable t, String msg, Object... args) {
        __logger.error(t, msg, args);
    }

    protected boolean __logTime = false;
    
    private II18nMessageResolver i18n = null;
    protected String __i18n(String key, Object ... args) {
        if (i18n == null) {
            i18n = __engine().conf().i18nMessageResolver();
        }
        return i18n.getMessage(this, key, args);
    }

    /**
     * The helper class to facilitate generating code for the "for" loop in
     * the template source
     *
     * @param <T>
     */
    protected static class __Itr<T> implements Iterable<T> {
        private Object _o = null;
        private int _size = -1;
        private Iterator<T> iterator = null;
        private int cursor = 0;

        public __Itr(T[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return ((T[]) _o)[cursor++];  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(int[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Integer) ((int[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(long[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Long) ((long[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(float[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Float) ((float[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(double[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Double) ((double[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(short[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Short) ((short[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(char[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Character) ((char[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(byte[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Byte) ((byte[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(boolean[] ta) {
            _o = ta;
            _size = ta.length;
            iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return cursor < _size;
                }

                @Override
                public T next() {
                    return (T) ((Boolean) ((boolean[]) _o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public __Itr(Range range) {
            _o = range;
            _size = range.size();
            iterator = range.iterator();
        }

        public __Itr(Object obj) {
            if (null == obj) {
                throw new NullPointerException();
            }
            String s = obj.toString();
            if (S.isEmpty(s)) {
                _o = "";
                _size = 0;
                iterator = Collections.EMPTY_LIST.iterator();
                return;
            }
            List<String> seps = new ArrayList<String>();
            RythmEngine engine = RythmEngine.get();
            RythmConfiguration conf = engine.conf();
            if ("zh".equals(conf.locale().getLanguage())) {
                seps.addAll(Arrays.asList("\n,、,，,；,。,：".split(",")));
            } else {
                seps.add("\n");
            }
            seps.addAll(Arrays.asList(";^,^:^_^-".split("\\^")));
            for (String sep : seps) {
                if (s.contains(sep)) {
                    List<String> ls = Arrays.asList(s.split(sep));
                    List<String> ls0 = new ArrayList<String>();
                    for (String s0 : ls) {
                        ls0.add(s0.trim());
                    }
                    _o = ls0;
                    _size = ls.size();
                    iterator = (Iterator<T>) ls0.iterator();
                    break;
                }
            }
            if (null == _o) {
                List<String> ls = new ArrayList<String>();
                ls.add(s);
                _size = 1;
                iterator = (Iterator<T>) ls.iterator();
            }
        }

        public __Itr(Iterable<T> tc) {
            _o = tc;
            if (tc instanceof Collection) {
                _size = ((Collection) tc).size();
            } else {
                int i = 0;
                for (Iterator itr = tc.iterator(); itr.hasNext(); itr.next()) {
                    i++;
                }
                _size = i;
            }

            iterator = tc.iterator();
        }

        public int size() {
            return _size;
        }

        @Override
        public Iterator<T> iterator() {
            return iterator;
        }
    }

}
