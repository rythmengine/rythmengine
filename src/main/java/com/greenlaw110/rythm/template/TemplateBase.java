package com.greenlaw110.rythm.template;

import com.alibaba.fastjson.JSON;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.FastRuntimeException;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.extension.ILang;
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
    private transient TemplateClass _templateClass = null;

    /**
     * Set template class and template lang to this template instance
     * <p/>
     * <p>Not to be called in user application or template</p>
     *
     * @param templateClass
     * @param lang
     */
    public void setTemplateClass(TemplateClass templateClass, ILang lang) {
        this._templateClass = templateClass;
        __ctx.init(this, lang);
    }

    /**
     * Return {@link S#INSTANCE String helper instance}. Could be used in
     * template authoring. For example:
     * <p/>
     * <pre><code>
     *
     * @return }
     *         }
     *         </code></pre>
     * @{ if (s().empty(userRight)) {
     */
    protected S s() {
        return S.INSTANCE;
    }

    /**
     * Return {@link #__engine rythm engine} that run this template. Could
     * be used in template authoring
     *
     * @return
     */
    protected RythmEngine r() {
        return _engine();
    }

    private Writer w;
    private OutputStream os;

    public void setWriter(Writer writer) {
        if (null == writer) throw new NullPointerException();
        if (null != os) throw new IllegalStateException("Cannot set writer to template when outputstream is presented");
        if (null != this.w)
            throw new IllegalStateException("Cannot set writer to template when an writer is presented");
        this.w = writer;
    }

    public void setOutputStream(OutputStream os) {
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
     * Alias of {@link #r()}
     *
     * @return
     */
    protected RythmEngine _engine() {
        return null == __engine ? Rythm.engine() : __engine;
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     */
    protected void _invokeTag(int line, String name) {
        __engine.invokeTag(line, name, this, null, null, null);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param ignoreNonExistsTag
     */
    protected void _invokeTag(int line, String name, boolean ignoreNonExistsTag) {
        __engine.invokeTag(line, name, this, null, null, null, ignoreNonExistsTag);
    }

    /**
     * Invoke a tag. Usually should not used directly in user template
     *
     * @param line
     * @param name
     * @param params
     */
    protected void _invokeTag(int line, String name, ITag.ParameterList params) {
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
    protected void _invokeTag(int line, String name, ITag.ParameterList params, boolean ignoreNonExistsTag) {
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
    protected void _invokeTag(int line, String name, ITag.ParameterList params, ITag.Body body) {
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
    protected void _invokeTag(int line, String name, ITag.ParameterList params, ITag.Body body, boolean ignoreNoExistsTag) {
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
    protected void _invokeTag(int line, String name, ITag.ParameterList params, ITag.Body body, ITag.Body context) {
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
    protected void _invokeTag(int line, String name, ITag.ParameterList params, ITag.Body body, ITag.Body context, boolean ignoreNonExistsTag) {
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
                __parent = (TemplateBase) pc.newInstance();
                //__parent.setTemplateClass(_engine().classes.getByClassName(pc.getName()));
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
     *
     * @param template
     * @param args
     * @return
     * @args String customTemplate, Map<String, Object> customParams
     * @{ Object renderResult = _render(customTemplate, customParams);
     * }
     * <p class="customer_content">@renderResult</p>
     * </code></pre>
     */
    protected RawData _render(String template, Object... args) {
        if (null == template) return new RawData("");
        return S.raw(__engine.sandbox().render(template, args));
    }

    /**
     * Render another template from within this template. Using the renderArgs
     * of this template.
     *
     * @param template
     * @return
     * @see #_render(String, Object...)
     */
    protected RawData _render(String template) {
        if (null == template) return new RawData("");
        return S.raw(__engine.sandbox().render(template, __renderArgs));
    }

    /**
     * Set layout content. Should not be used in user application or template
     *
     * @param body
     */
    protected final void setLayoutContent(String body) {
        layoutContent = body;
    }

    /**
     * Add layout section. Should not be used in user application or template
     *
     * @param name
     * @param section
     */
    private void addLayoutSection(String name, String section) {
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
    protected void _startSection(String name) {
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
    protected void _endSection() {
        _endSection(false);
    }

    /**
     * End a layout section with a boolean flag mark if it is a default content or not.
     * Not to be used in user application or template
     *
     * @param def
     */
    protected void _endSection(boolean def) {
        if (null == tmpOut && null == tmpCaller) throw new IllegalStateException("section has not been started");
        if (!def) {
            addLayoutSection(section, __buffer.toString());
        }
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
    protected void _pLayoutSection(String name) {
        p(layoutSections.get(name));
    }

    /**
     * Get a section content as {@link RawData} by name. Not to be used in user application or template
     *
     * @param name
     * @return
     */
    protected RawData _getSection(String name) {
        return S.raw(layoutSections.get(name));
    }

    /**
     * Get layout content as {@link RawData}. Not to be used in user application or template
     *
     * @return
     */
    protected RawData _getSection() {
        return S.raw(S.isEmpty(layoutContent) ? layoutSections.get("__CONTENT__") : layoutContent);
    }

    /**
     * Print the layout content. Not to be used in user application or template
     */
    protected void _pLayoutContent() {
        p(_getSection());
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
     * @return
     */
    protected TemplateBase internalClone() {
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
     * @return
     */
    @Override
    public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
        if (null == engine) throw new NullPointerException();
        TemplateBase tmpl = internalClone();
        if (tmpl.__parent != null) {
            tmpl.__parent = (TemplateBase) tmpl.__parent.cloneMe(engine, caller);
        }
        tmpl.__engine = engine;
        tmpl._templateClass = _templateClass;
        if (null != caller) {
            tmpl.__caller = (TextBuilder) caller;
        }
        tmpl.__ctx = new Context(__ctx);
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
        return tmpl;
    }

    /**
     * Not to be used in user application or template
     */
    protected void internalInit() {
        loadExtendingArgs();
        init();
    }

    /**
     * the implementation of this method is to be generated by {@link com.greenlaw110.rythm.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    protected void setup() {
    }

    /**
     * the implementation of this method is to be generated by {@link com.greenlaw110.rythm.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    protected void loadExtendingArgs() {
    }

    /**
     * the implementation of this method is to be generated by {@link com.greenlaw110.rythm.internal.CodeBuilder}.
     * Not to be used in user application or template
     */
    @Override
    public void init() {
    }

    private boolean _logTime() {
        return __logger.isDebugEnabled() && (__logTime || __engine.conf().logRenderTime());
    }

    /**
     * Get the template class of this template. Not to be used in user application or template
     *
     * @param useCaller
     * @return
     */
    public TemplateClass getTemplateClass(boolean useCaller) {
        TemplateClass tc = _templateClass;
        if (useCaller && null == tc) {
            TemplateBase caller = _caller();
            if (null != caller) return caller.getTemplateClass(true);
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
        setOutputStream(os);
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
        setWriter(w);
        render();
    }

    /**
     * Render and return result in String. This method is usually called from API defined in
     * {@link RythmEngine}
     */
    @Override
    public final String render() {
        RythmEngine engine = _engine();
        RythmEngine.set(engine);
        try {
            long l = 0l;
            if (_logTime()) {
                l = System.currentTimeMillis();
            }

            RythmEvents.ON_RENDER.trigger(engine, this);
            setup();
            if (_logTime()) {
                __logger.debug("< preprocess [%s]: %sms", getClass().getName(), System.currentTimeMillis() - l);
                l = System.currentTimeMillis();
            }
            String s = internalRender();
            if (_logTime()) {
                __logger.debug("<<<<<<<<<<<< [%s] total render: %sms", getClass().getName(), System.currentTimeMillis() - l);
            }
            return s;
        } catch (ClassReloadException e) {
            if (__logger.isDebugEnabled()) {
                __logger.debug("Cannot hotswap class, try to restart engine...");
            }
            engine.restart(e);
            return render();
        } catch (ClassCastException e) {
            if (__logger.isDebugEnabled()) {
                __logger.debug("ClassCastException found, force refresh template and try again...");
            }
            TemplateClass tc = engine.classes().getByClassName(getClass().getName());
            tc.refresh(true);
            ITemplate t = tc.asTemplate(__ctx.currentLang());
            return t.render();
        }
    }

    private Writer w_ = null;

    /**
     * Set output file path
     *
     * @param path
     */
    protected void _setOutput(String path) {
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
    protected void _setOutput(File file) {
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
    protected void _setOutput(OutputStream os) {
        w_ = new OutputStreamWriter(os);
    }

    /**
     * Set output writer
     *
     * @param w
     */
    protected void _setOutput(Writer w) {
        w_ = w;
    }

    /**
     * Not to be used in user application or template
     */
    protected void internalBuild() {
        w_ = null; // reset output destination
        //if (!(engine.recordTemplateSourceOnError || engine.recordJavaSourceOnError)) {
        if (false) {
            internalInit();
            build();
        } else {
            try {
                try {
                    long l = 0l;
                    if (_logTime()) {
                        l = System.currentTimeMillis();
                    }
                    internalInit();
                    build();
                    if (_logTime()) {
                        __logger.debug("<<<<<<<<<<<< [%s] build: %sms", getClass().getName(), System.currentTimeMillis() - l);
                    }
                } catch (RythmException e) {
                    throw e;
                } catch (Exception e) {
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
                                    //System.out.println("<<<<<" + msg);
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
            } catch (RuntimeException e) {
                // try to restart engine
                if (!Rythm.insideSandbox()) {
                    try {
                        __engine.restart(e);
                    } catch (RuntimeException e0) {
                        // ignore it because we already thrown it out
                    }
                }
                throw e;
            }
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
    protected String internalRender() {
        internalBuild();
        if (null != __parent && __parent != this) {
            __parent.setLayoutContent(toString());
            __parent.addAllLayoutSections(layoutSections);
            __parent.addAllRenderProperties(renderProperties);
            __parent.setRenderArgs(__renderArgs);
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
     * @return
     */
    @Override
    public TextBuilder build() {
        return this;
    }

    /**
     * Return render arg type in array. Not to be used in user application or template
     */
    protected Class[] renderArgTypeArray() {
        return null;
    }

    /**
     * Return render arg type in Map. Not to be used in user application or template
     *
     * @return
     */
    protected Map<String, Class> renderArgTypeMap() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public void setRenderArgs(Map<String, Object> args) {
        __renderArgs.putAll(args);
    }

    @Override
    public void setRenderArg(JSONWrapper jsonData) {
        if (jsonData.isArray()) {
            setJSONArray(jsonData.getArray());
        } else {
            setJSONObject(jsonData.getObject());
        }
    }

    private void setJSONArray(List<Object> jsonArray) {
        Class[] types = renderArgTypeArray();
        List<Object> args = new ArrayList<Object>(types.length);
        int paraNo = jsonArray.size();
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
            setRenderArg(i, p);
        }
    }

    private void setJSONObject(Map<String, Object> jsonObject) {
        Map<String, Class> types = renderArgTypeMap();
        for (String nm : jsonObject.keySet()) {
            if (types.containsKey(nm)) {
                Class c = types.get(nm);
                Object o = jsonObject.get(nm);
                Object p;
                if (o instanceof List) {
                    p = JSON.parseArray(o.toString(), c);
                } else {
                    p = JSON.parseObject(o.toString(), c);
                }
                setRenderArg(nm, p);
            }
        }
    }

    /**
     * Set render arg from {@link ITag.ParameterList tag params}
     * Not to be used in user application or template
     *
     * @param params
     */
    protected void setRenderArgs(ITag.ParameterList params) {
        for (int i = 0; i < params.size(); ++i) {
            ITag.Parameter param = params.get(i);
            if (null != param.name) setRenderArg(param.name, param.value);
            else setRenderArg(i, param.value);
        }
    }

    @Override
    public void setRenderArgs(Object... args) {
    }

    @Override
    public void setRenderArg(String name, Object arg) {
        __renderArgs.put(name, arg);
    }

    /**
     * alias of {@link #setRenderArg(String, Object)}
     *
     * @param name
     * @param arg
     */
    protected final void _set(String name, Object arg) {
        setRenderArg(name, arg);
    }

    /**
     * Return caller of the template when this template is
     * invoked as a {@link ITag tag}
     *
     * @return
     */
    protected final TemplateBase _caller() {
        return null == __caller ? null : (TemplateBase) __caller;
    }

    @Override
    public <T> T getRenderArg(String name) {
        Object val = __renderArgs.get(name);
        return (T) (null != val ? val : (null != __caller ? caller().getRenderArg(name) : null));
    }

    /**
     * Alias of {@link #getRenderArg(String)}
     *
     * @param name
     * @param <T>
     * @return
     */
    protected final <T> T _get(String name) {
        return getRenderArg(name);
    }

    /**
     * Get render arg and do type cast to the class specified
     *
     * @param name
     * @param c
     * @param <T>
     * @return
     */
    protected final <T> T _getAs(String name, Class<T> c) {
        Object o = getRenderArg(name);
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
     * @return
     */
    protected final <T> T _getRenderProperty(String name, T def) {
        Object o = renderProperties.get(name);
        return (T) (null == o ? def : o);
    }

    /**
     * Get render property by name. This is usually called by <code>@get()</code> directive in teh layout template.
     *
     * @param name
     * @param <T>
     * @return
     * @see #_setRenderProperty(String, Object)
     */
    protected final <T> T _getRenderProperty(String name) {
        return (T) _getRenderProperty(name, null);
    }

    /**
     * Get render property by name and do type cast to the specified default value.
     * If the render property cannot be found by name, then return the default value
     *
     * @param name
     * @param def
     * @param <T>
     * @return
     * @see #_getRenderProperty(String)
     */
    protected final <T> T _getRenderPropertyAs(String name, T def) {
        Object o = _getRenderProperty(name, def);
        return null == o ? def : (T) o;
    }

    /**
     * Set render property by name. This is pass value from sub (content) template
     * to parent (layout) template Usually this is invoked by <code>@set()</code>
     * directive in the sub template
     *
     * @param name
     * @param val
     * @see #_getRenderProperty(String)
     * @see #_getRenderProperty(String, Object)
     */
    protected final void _setRenderProperty(String name, Object val) {
        renderProperties.put(name, val);
    }

    /**
     * Handle template execution exception. Not to be called in user application or template
     *
     * @param e
     */
    protected final void handleTemplateExecutionException(Exception e) {
        try {
            if (!(Boolean) RythmEvents.ON_RENDER_EXCEPTION.trigger(_engine(), F.T2(this, e))) {
                throw e;
            }
        } catch (RuntimeException e0) {
            throw e0;
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public void setRenderArg(int position, Object arg) {
    }

    /**
     * The render context
     */
    protected Context __ctx = new Context();

    /**
     * Return current template lang. Not to be used in user application or template
     *
     * @return
     */
    public ILang __curLang() {
        return __ctx.currentLang();
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
    protected void append(StrBuf wrapper) {
        if (appendToBuffer()) {
            super.append(wrapper);
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
    protected void append(Object o) {
        if (appendToBuffer()) super.append(o);

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
    protected void append(char c) {
        if (appendToBuffer()) super.append(c);

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
    protected void append(int i) {
        if (appendToBuffer()) super.append(i);

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
    protected void append(long l) {
        if (appendToBuffer()) super.append(l);

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
    protected void append(float f) {
        if (appendToBuffer()) super.append(f);

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
    protected void append(double d) {
        if (appendToBuffer()) super.append(d);

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
    protected void append(boolean b) {
        if (appendToBuffer()) super.append(b);

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
    public Escape defaultEscape() {
        return __ctx.currentEscape();
    }

    @Override
    public final TemplateBase pe(Object o) {
        return (TemplateBase) super.pe(o);
    }

    @Override
    public final TemplateBase pe(Object o, ITemplate.Escape escape) {
        return (TemplateBase) super.pe(o, escape);
    }

    // --- debugging interface
    protected static void _log(String msg, Object... args) {
        __logger.info(msg, args);
    }

    protected static void _debug(String msg, Object... args) {
        __logger.debug(msg, args);
    }

    protected static void _info(String msg, Object... args) {
        __logger.info(msg, args);
    }

    protected static void _warn(String msg, Object... args) {
        __logger.error(msg, args);
    }

    protected static void _warn(Throwable t, String msg, Object... args) {
        __logger.error(t, msg, args);
    }

    protected static void _error(String msg, Object... args) {
        __logger.error(msg, args);
    }

    protected static void _error(Throwable t, String msg, Object... args) {
        __logger.error(t, msg, args);
    }

    protected boolean __logTime = false;

    /**
     * The helper class to facilitate generating code for the "for" loop in
     * the template source
     *
     * @param <T>
     */
    protected static class _Itr<T> implements Iterable<T> {
        private Object _o;
        private int _size = -1;
        private Iterator<T> iterator = null;
        private int cursor = 0;

        public _Itr(T[] ta) {
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

        public _Itr(int[] ta) {
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

        public _Itr(long[] ta) {
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

        public _Itr(float[] ta) {
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

        public _Itr(double[] ta) {
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

        public _Itr(short[] ta) {
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

        public _Itr(char[] ta) {
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

        public _Itr(byte[] ta) {
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

        public _Itr(boolean[] ta) {
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
        
        public _Itr(Range range) {
            _o = range;
            _size = range.size();
            iterator = range.iterator();
        }

        public _Itr(Iterable<T> tc) {
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
