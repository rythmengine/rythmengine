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
package org.rythmengine.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.Sandbox;
import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.exception.FastRuntimeException;
import org.rythmengine.exception.RythmException;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.II18nMessageResolver;
import org.rythmengine.internal.IEvent;
import org.rythmengine.internal.RythmEvents;
import org.rythmengine.internal.TemplateBuilder;
import org.rythmengine.internal.compiler.ClassReloadException;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.logger.ILogger;
import org.rythmengine.logger.Logger;
import org.rythmengine.utils.*;

import java.io.*;
import java.lang.reflect.Array;
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
     * Set template class and template code type to this template instance
     * <p/>
     * <p>Not to be called in user application or template</p>
     *
     * @param templateClass
     */
    public void __setTemplateClass(TemplateClass templateClass) {
        this.__templateClass = templateClass;
    }

    public void __prepareRender(ICodeType type, Locale locale, RythmEngine engine) {
        TemplateClass tc = __templateClass;
        __ctx.init(this, type, locale, tc, engine);
        Class<? extends TemplateBase> c = getClass();
        Class<?> pc = c.getSuperclass();
        if (TemplateBase.class.isAssignableFrom(pc) && !Modifier.isAbstract(pc.getModifiers())) {
            try {
                TemplateClass ptc = engine.classes().getByClassName(pc.getName());
                if (null != ptc) {
                    __parent = (TemplateBase) ptc.asTemplate(engine);
                } else {
                    throw new RuntimeException("Cannot find template class for parent class: " + pc);
                }
                //__parent.__setTemplateClass(__engine().classes.getByClassName(pc.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (null != __parent) {
            __parent.__ctx.init(__parent, type, locale, tc, engine);
        }
    }

    /**
     * Return {@link org.rythmengine.utils.S#INSTANCE String helper instance}. Could be used in
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

    @Override
    public ITemplate __setWriter(Writer writer) {
        if (null == writer) throw new NullPointerException();
        if (null != os) throw new IllegalStateException("Cannot set writer to template when outputstream is presented");
        if (null != this.w)
            throw new IllegalStateException("Cannot set writer to template when an writer is presented");
        this.w = writer;
        return this;
    }

    @Override
    public ITemplate __setOutputStream(OutputStream os) {
        if (null == os) throw new NullPointerException();
        if (null != w) throw new IllegalStateException("Cannot set output stream to template when writer is presented");
        if (null != this.os)
            throw new IllegalStateException("Cannot set output stream to template when an outputstream is presented");
        this.os = os;
        return this;
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
        __engine.invokeTemplate(line, name, this, null, null, null);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param ignoreNonExistsTag
     */
    protected void __invokeTag(int line, String name, boolean ignoreNonExistsTag) {
        __engine.invokeTemplate(line, name, this, null, null, null, ignoreNonExistsTag);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     */
    protected void __invokeTag(int line, String name, ITag.__ParameterList params) {
        __engine.invokeTemplate(line, name, this, params, null, null);
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
        __engine.invokeTemplate(line, name, this, params, null, null, ignoreNonExistsTag);
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
        __engine.invokeTemplate(line, name, this, params, body, null);
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
        __engine.invokeTemplate(line, name, this, params, body, null, ignoreNoExistsTag);
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
        __engine.invokeTemplate(line, name, this, params, body, context);
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
        __engine.invokeTemplate(line, name, this, params, body, context, ignoreNonExistsTag);
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
     * @return render result as {@link org.rythmengine.utils.RawData}
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
     * Get a section content as {@link org.rythmengine.utils.RawData} by name. Not to be used in user application or template
     *
     * @param name
     * @return section data by name
     */
    protected RawData __getSection(String name) {
        return S.raw(layoutSections.get(name));
    }

    /**
     * Get layout content as {@link org.rythmengine.utils.RawData}. Not to be used in user application or template
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
        //tmpl.__templateClass = __templateClass;
        tmpl.__ctx = new __Context();
        //if (null != buffer) tmpl.__buffer = buffer;
        if (null != __buffer) tmpl.__buffer = new StringBuilder();
        tmpl.__renderArgs = new HashMap<String, Object>(__renderArgs.size());
        //tmpl.layoutContent = "";
        tmpl.layoutSections = new HashMap<String, String>();
        tmpl.renderProperties = new HashMap<String, Object>();
        //tmpl.section = null;
        //tmpl.tmpCaller = null;
        //tmpl.tmpOut = null;
        //tmpl.__logTime = __logTime;
        //tmpl.w = null;
        //tmpl.os = null;
        if (null != caller) {
            tmpl.__caller = (TextBuilder) caller;
            Map<String, Object> callerRenderArgs = new HashMap<String, Object>(((TemplateBase) caller).__renderArgs);
            Map<String, Class> types = tmpl.__renderArgTypeMap();
            for (String s : callerRenderArgs.keySet()) {
                if (tmpl.__renderArgs.containsKey(s)) continue;
                Object o = callerRenderArgs.get(s);
                if (null == o || __isDefVal(o)) continue;
                Class<?> c = types.get(s);

                if (null == c || c.isAssignableFrom(o.getClass())) {
                    tmpl.__setRenderArg(s, o);
                }
            }
        }
        tmpl.__setUserContext(engine.renderSettings.userContext());
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
     * the implementation of this method is to be generated by {@link org.rythmengine.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    protected void __setup() {
    }

    /**
     * the implementation of this method is to be generated by {@link org.rythmengine.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    protected void __loadExtendingArgs() {
    }

    /**
     * the implementation of this method is to be generated by {@link org.rythmengine.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    @Override
    public void __init() {
    }

    private boolean __logTime() {
        return false;
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
     *
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
        boolean engineSet = RythmEngine.set(engine);
        try {
            long l = 0l;
            boolean logTime = __logTime();
            if (logTime) {
                l = System.currentTimeMillis();
            }

            __triggerRenderEvent(RythmEvents.ON_RENDER, engine);
            __setup();
            if (logTime) {
                __logger.debug("< preprocess [%s]: %sms", getClass().getName(), System.currentTimeMillis() - l);
                l = System.currentTimeMillis();
            }
            try {
                String s = __internalRender();
                return s;
            } finally {
                __triggerRenderEvent(RythmEvents.RENDERED, engine);
                if (logTime) {
                    __logger.debug("<<<<<<<<<<<< [%s] total render: %sms", getClass().getName(), System.currentTimeMillis() - l);
                }
            }
        } catch (ClassReloadException e) {
            engine.restart(e);
            return render();
        } finally {
            if (engineSet) {
                RythmEngine.remove();
            }
        }
    }

    private String secureCode = null;

    @Override
    public ITemplate __setSecureCode(String secureCode) {
        if (null == secureCode) return this;
        this.secureCode = secureCode;
        if (null != __parent) {
            __parent.__setSecureCode(secureCode);
        }
        return this;
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

    private static final ThreadLocal<Boolean> cce_ = new ThreadLocal<Boolean>() {
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
        try {
            long l = 0l;
            if (__logTime()) {
                l = System.currentTimeMillis();
            }
            final String code = secureCode;
            Sandbox.enterRestrictedZone(code);
            try {
                __internalInit();
                build();
            } finally {
                Sandbox.leaveCurZone(code);
            }
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
     * The {@link org.rythmengine.internal.CodeBuilder} will generate the
     * implementation of this method usually
     *
     * @return this template as a {@link org.rythmengine.utils.TextBuilder}
     */
    @Override
    public TextBuilder build() {
        return this;
    }

    private Map<String, Object> userCtx;

    @Override
    public Map<String, Object> __getUserContext() {
        return null == userCtx ? Collections.EMPTY_MAP : userCtx;
    }

    @Override
    public ITemplate __setUserContext(Map<String, Object> context) {
        this.userCtx = context;
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
     *
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
    public ITemplate __setRenderArgs(Map<String, Object> args) {
        __renderArgs.putAll(args);
        return this;
    }

    @Override
    public ITemplate __setRenderArg(JSONWrapper jsonData) {
        if (jsonData.isArray()) {
            setJSONArray(jsonData.getArray());
        } else {
            setJSONObject(jsonData.getObject());
        }
        return this;
    }

    private void setJSONArray(List<Object> jsonArray) {
        Class[] types = __renderArgTypeArray();
        int paraNo = jsonArray.size();
        if (types.length == 1 && types[0].equals(List.class)) {
            Map<String, Class> typeMap = __renderArgTypeMap();
            String vn = __renderArgName(0);
            Class c = typeMap.get(vn + "__0");
            if (null == c) {
                // an array type
                c = typeMap.get(vn);
            }
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
                    //String vn = nm;
                    Class c0 = typeMap.get(nm + "__0");
                    boolean isArray = false;
                    if (null == c0) {
                        // an array type
                        isArray = true;
                        c0 = typeMap.get(nm);
                    }
                    if (isArray) {
                        JSONArray l = (JSONArray) o;
                        //try {
                        Class c1 = c0.getComponentType();
                        int size = l.size();
                        Object a = Array.newInstance(c1, size);
                        for (int i = 0; i < size; ++i) {
                            Object el = l.get(i);
                            el = JSON.parseObject(el.toString(), c1);
                            Array.set(a, i, el);
                        }
                        //}
                        p = a;
                    } else {
                        p = JSON.parseArray(o.toString(), c0);
                    }
                } else {
                    String s = o.toString();
                    if (String.class.equals(c)) {
                        p = s;
                    } else {
                        p = JSON.parseObject(s, c);
                    }
                }
                __setRenderArg(nm, p);
            }
        }
    }

    /**
     * Set render arg from {@link org.rythmengine.template.ITag.__ParameterList tag params}
     * Not to be used in user application or template
     *
     * @param params
     * @return this template instance
     */
    protected TemplateBase __setRenderArgs0(ITag.__ParameterList params) {
        for (int i = 0; i < params.size(); ++i) {
            ITag.__Parameter param = params.get(i);
            if (null != param.name) __setRenderArg(param.name, param.value);
            else __setRenderArg(i, param.value);
        }
        return this;
    }

    @Override
    public ITemplate __setRenderArgs(Object... args) {
        return this;
    }

    @Override
    public ITemplate __setRenderArg(String name, Object arg) {
        __renderArgs.put(name, arg);
        return this;
    }

    /**
     * alias of {@link #__setRenderArg(String, Object)}
     *
     * @param name
     * @param arg
     * @return this template instance
     */
    protected final TemplateBase __set(String name, Object arg) {
        __setRenderArg(name, arg);
        return this;
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
        //if (null == val) return null;
        if (null != __caller) {
            if (!__isDefVal(val)) return (T)val;
            else return caller().__getRenderArg(name);
        } else {
            return (T) val;
        }
    }
    
    protected final static <T> T __get(Map<String, Object> map, String name, Class<T> cls) {
        Object o = map.get(name);
        return (null != o) ? (T) o : __transNull(cls);
    }
    
    protected final <T> T __get(String name, Class<T> cls) {
        Object o = __get(name);
        return (null != o) ? (T) o : __transNull(cls);
    }
    
    protected final static <T> T __safeCast(Object o, Class<T> cls) {
        return (null != o) ? (T) o : __transNull(cls);
    }
    
    protected final static <T> T __transNull(Class<T> cls) {
        if (null == cls) return null; 
        if (cls.equals(String.class)) {
            return (T) "";
        } if (cls.equals(Integer.class)) {
            return (T)Integer.valueOf(0);
        } else if (cls.equals(Boolean.class)) {
            return (T) Boolean.valueOf(false);
        } else if (cls.equals(Double.class)) {
            return (T) Double.valueOf(0);
        } else if (cls.equals(Float.class)) {
            return (T) Float.valueOf(0);
        } else if (cls.equals(Long.class)) {
            return (T) Long.valueOf(0);
        } else if (cls.equals(Short.class)) {
            return (T) Short.valueOf((short)0);
        } else if (cls.equals(Byte.class)) {
            return (T) Byte.valueOf((byte) 0);
        } else if (cls.equals(Character.class)) {
            return (T) Character.valueOf((char) 0);
        }
        return null;
    }
    
    protected final static boolean __isDefVal(Object o) {
        if (null == o) return true;
        if (o instanceof String) return ("".equals(o));
        if (o instanceof Boolean) return (Boolean.valueOf(false).equals(o));
        if (o instanceof Integer) return (Integer.valueOf(0).equals(o));
        if (o instanceof Double) return (Double.valueOf(0).equals(o));
        if (o instanceof Float) return (Float.valueOf(0).equals(o));
        if (o instanceof Long) return (Long.valueOf(0).equals(o));
        if (o instanceof Short) return (Short.valueOf((short)0).equals(o));
        if (o instanceof Byte) return (Byte.valueOf((byte)0).equals(o));
        if (o instanceof Character) return (Character.valueOf((char)0).equals(o));
        return false;
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
        Object o = __renderArgs.get(name);
        return (T) (__isDefVal(o) ? def : o);
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
        __renderArgs.put(name, val);
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
    public ITemplate __setRenderArg(int position, Object arg) {
        return this;
    }

    /**
     * The render context
     */
    protected __Context __ctx = null;//new __Context();

    public ICodeType __curCodeType() {
        return __ctx.currentCodeType();
    }

    public Locale __curLocale() {
        return __ctx.currentLocale();
    }

    public Escape __curEscape() {
        return __ctx.currentEscape();
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
        }
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
            try {
                w.write(wrapper.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void __append(Object o) {
        String oStr = o.toString();
        if (appendToBuffer()) super.__append(oStr);
        if (null == os && null == w) return;

        StrBuf wrapper = new StrBuf(oStr);
        if (appendToOutputStream()) {
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            try {
                os.write(c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(i));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(l));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(f));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(d));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
        if (null == os && null == w) return;

        if (appendToOutputStream()) {
            StrBuf wrapper = new StrBuf(String.valueOf(b));
            try {
                os.write(wrapper.toBinary());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appendToWriter()) {
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
    
    protected final Class __getClass(int o) {
        return int.class;
    }
    
    protected final Class __getClass(boolean o) {
        return boolean.class;
    }
    
    protected final Class __getClass(char o) {
        return char.class;
    }

    protected final Class __getClass(long o) {
        return long.class;
    }

    protected final Class __getClass(float o) {
        return float.class;
    }
    
    protected final Class __getClass(double o) {
        return double.class;
    }

    protected final Class __getClass(byte o) {
        return byte.class;
    }
    
    protected final Class __getClass(Object o) {
        if (null == o) return Void.TYPE;
        return o.getClass();
    }
    
    protected final Object __eval(String expr) {
        Map<String, Object> ctx = new HashMap<String, Object>(__renderArgs);
        ctx.putAll(itrVars());
        try {
            return __engine().eval(expr, this, ctx);
        } catch (RuntimeException e) {
            return null;
        }
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

    protected String __i18n(String key, Object... args) {
        if (i18n == null) {
            i18n = __engine().conf().i18nMessageResolver();
        }
        return i18n.getMessage(this, key, args);
    }

    private Stack<F.T2<String, Object>> itrVars = new Stack<F.T2<String, Object>>();
    
    protected void __pushItrVar(String name, Object val) {
        itrVars.push(F.T2(name, val));
    }
    
    protected void __popItrVar() {
        if (itrVars.isEmpty()) return;
        itrVars.pop();
    }
    
    private Map<String, Object> itrVars() {
        if (itrVars.isEmpty()) return Collections.EMPTY_MAP;
        if (itrVars.size() == 1) return itrVars.peek().asMap();
        Stack<F.T2<String, Object>> tmp = new Stack<F.T2<String, Object>>();
        Map<String, Object> m = new HashMap<String, Object>();
        Stack<F.T2<String, Object>> vs = itrVars;
        while (!vs.isEmpty()) {
            F.T2<String, Object> var = vs.pop();
            tmp.push(var);
            String k = var._1;
            if (!m.containsKey(k)) {
                m.put(k, var._2);
            }
        }
        while (!tmp.isEmpty()) {
            vs.push(tmp.pop());
        }
        return m;
    }

    /**
     * The helper class to facilitate generating code for the "for" loop in
     * the template source
     *
     * @param <T>
     */
    protected static class __Itr<T> implements Iterable<T> {
        protected Object _o = null;
        protected int _size = -1;
        protected Iterator<T> iterator = null;
        protected int cursor = 0;
        
        private static final Iterator nullIterator = new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }

            @Override
            public void remove() {
                return;
            }
        };
        

        private static final __Itr EMPTY_ITR = new __Itr(){
            @Override
            public int size() {
                return 0;
            }

            @Override
            public Iterator iterator() {
                return nullIterator;
            }
        }; 
        
        private __Itr() {}
        
        private __Itr(Object o) {
            //this._o = o;
            if (o.getClass().isArray()) {
                _size = Array.getLength(o);
                iterator = new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return cursor < _size;
                    }

                    @Override
                    public T next() {
                        return (T)Array.get(_o, cursor++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            } else {
                String s = o.toString();
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
        }

        public static <T> __Itr<T> valueOf(T[] ta) {
            final __Itr<T> itr = new __Itr<T>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public T next() {
                    return ((T[]) itr._o)[itr.cursor++]; 
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Integer> valueOf(int[] ta) {
            final __Itr<Integer> itr = new __Itr<Integer>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Integer>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Integer next() {
                    return ((int[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Long> valueOf(long[] ta) {
            final __Itr<Long> itr = new __Itr<Long>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Long>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Long next() {
                    return ((long[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Float> valueOf(float[] ta) {
            final __Itr<Float> itr = new __Itr<Float>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Float>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Float next() {
                    return ((float[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Double> valueOf(double[] ta) {
            final __Itr<Double> itr = new __Itr<Double>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Double>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Double next() {
                    return ((double[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Short> valueOf(short[] ta) {
            final __Itr<Short> itr = new __Itr<Short>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Short>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Short next() {
                    return ((short[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Character> valueOf(char[] ta) {
            final __Itr<Character> itr = new __Itr<Character>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Character>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Character next() {
                    return ((char[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Byte> valueOf(byte[] ta) {
            final __Itr<Byte> itr = new __Itr<Byte>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Byte>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Byte next() {
                    return ((byte[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static __Itr<Boolean> valueOf(boolean[] ta) {
            final __Itr<Boolean> itr = new __Itr<Boolean>();
            itr._o = ta;
            itr._size = ta.length;
            itr.iterator = new Iterator<Boolean>() {
                @Override
                public boolean hasNext() {
                    return itr.cursor < itr._size;
                }

                @Override
                public Boolean next() {
                    return ((boolean[]) itr._o)[itr.cursor++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return itr;
        }

        public static <T extends Comparable> __Itr<T> valueOf(Range range) {
            __Itr<T> itr = new __Itr<T>();
            itr._o = range;
            itr._size = range.size();
            itr.iterator = range.iterator();
            return itr;
        }
        
        public static __Itr valueOf(final Object obj) {
            if (null == obj) {
                return EMPTY_ITR;
            }
            if (obj instanceof Iterable) {
                return valueOf((Iterable)obj);
            }
            Class c = obj.getClass();
            if (c.isArray()) {
                Class ct = c.getComponentType();
                if (ct.equals(int.class)) {
                    return valueOf((int[])obj);
                } else if (ct.equals(long.class)) {
                    return valueOf((long[])obj);
                } else if (ct.equals(float.class)) {
                    return valueOf((float[])obj);
                } else if (ct.equals(double.class)) {
                    return valueOf((double[])obj);
                } else if (ct.equals(char.class)) {
                    return valueOf((char[])obj);
                } else if (ct.equals(byte.class)) {
                    return valueOf((byte[])obj);
                } else if (ct.equals(boolean.class)) {
                    return valueOf((boolean[])obj);
                }
            }
            return new __Itr(obj);
        }

        public static <T> __Itr valueOf(Iterable<T> tc) {
            final __Itr<T> itr = new __Itr<T>();
            itr._o = tc;
            if (tc instanceof Collection) {
                itr._size = ((Collection) tc).size();
            } else {
                itr._size = -1;
            }

            itr.iterator = tc.iterator();
            return itr;
        }
        
        public int size() {
            return _size;
        }

        @Override
        public Iterator<T> iterator() {
            return iterator;
        }
    }

    public static void main(String[] args) {
        Integer[] ia = new Integer[]{1, 2};
        Object o = ia;
        __Itr<Integer> it = __Itr.valueOf(o);
        for (int i : it) {
            System.out.println(i);
        }
    }

}
