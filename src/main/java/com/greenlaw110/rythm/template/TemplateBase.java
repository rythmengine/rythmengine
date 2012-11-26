package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.FastRuntimeException;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.internal.TemplateBuilder;
import com.greenlaw110.rythm.internal.compiler.ClassReloadException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.utils.IO;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public abstract class TemplateBase extends TemplateBuilder implements ITemplate {

    protected final ILogger logger = Logger.get(TemplateBase.class);

    protected transient RythmEngine engine = null;
    private transient TemplateClass templateClass = null;
    public void setTemplateClass(TemplateClass templateClass) {
        this.templateClass = templateClass;
        __ctx.init(this);
    }

    protected S s() {
        return S.INSTANCE;
    }

    protected RythmEngine r() {
        return engine;
    }

    protected Map<String, Object> _properties = new HashMap<String, Object>();

    protected RythmEngine _engine() {
        return null == engine ? Rythm.engine : engine;
    }

    protected void _invokeTag(String name) {
        engine.invokeTag(name, this, null, null, null);
    }

    protected void _invokeTag(String name, boolean ignoreNonExistsTag) {
        engine.invokeTag(name, this, null, null, null, ignoreNonExistsTag);
    }

    protected void _invokeTag(String name, ITag.ParameterList params) {
        engine.invokeTag(name, this, params, null, null);
    }

    protected void _invokeTag(String name, ITag.ParameterList params, boolean ignoreNonExistsTag) {
        engine.invokeTag(name, this, params, null, null, ignoreNonExistsTag);
    }

    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body) {
        engine.invokeTag(name, this, params, body, null);
    }

    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body, boolean ignoreNoExistsTag) {
        engine.invokeTag(name, this, params, body, null, ignoreNoExistsTag);
    }

    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body, ITag.Body context) {
        engine.invokeTag(name, this, params, body, context);
    }

    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body, ITag.Body context, boolean ignoreNonExistsTag) {
        engine.invokeTag(name, this, params, body, context, ignoreNonExistsTag);
    }

    /* to be used by dynamic generated sub classes */
    private String layoutContent = "";
    private Map<String, String> layoutSections = new HashMap<String, String>();
    private Map<String, Object> renderProperties = new HashMap<String, Object>();

    protected TemplateBase __parent = null;

    public TemplateBase() {
        super();
        Class<? extends TemplateBase> c = getClass();
        Class<?> pc = c.getSuperclass();
        if (TemplateBase.class.isAssignableFrom(pc) && !Modifier.isAbstract(pc.getModifiers())) {
            try {
                __parent = (TemplateBase) pc.newInstance();
                __parent.setTemplateClass(_engine().classes.getByClassName(pc.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected RawData _render(String template, Object... args) {
        if (null == template) return new RawData("");
        return S.raw(engine.render(template, args));
    }

    protected RawData _render(String template) {
        if (null == template) return new RawData("");
        return S.raw(engine.render(template, _properties));
    }

    protected final void setLayoutContent(String body) {
        layoutContent = body;
    }

    private void addLayoutSection(String name, String section, boolean def) {
        if (def  && layoutSections.containsKey(name)) return;
        layoutSections.put(name, section);
    }

    private StringBuilder tmpOut = null;
    private String section = null;
    private TextBuilder tmpCaller = null;

    protected void _startSection(String name) {
        if (null == name) throw new NullPointerException("section name cannot be null");
        if (null != tmpOut) throw new IllegalStateException("section cannot be nested");
        tmpCaller = _caller;
        _caller = null;
        tmpOut = _out;
        _out = new StringBuilder();
        section = name;
    }

    protected void _endSection() {
        _endSection(false);
    }

    protected void _endSection(boolean def) {
        if (null == tmpOut && null == tmpCaller) throw new IllegalStateException("section has not been started");
        addLayoutSection(section, _out.toString(), def);
        _out = tmpOut;
        _caller = tmpCaller;
        tmpOut = null;
        tmpCaller = null;
    }

    protected void _pLayoutSection(String name) {
        p(layoutSections.get(name));
    }

    protected RawData _getSection(String name) {
        return S.raw(layoutSections.get(name));
    }

    protected RawData _getSection() {
        return S.raw(S.isEmpty(layoutContent) ? layoutSections.get("__CONTENT__") : layoutContent);
    }

    protected void _pLayoutContent() {
        p(_getSection());
    }

    private void addAllLayoutSections(Map<String, String> sections) {
        if (null != sections) layoutSections.putAll(sections);
    }

    private void addAllRenderProperties(Map<String, Object> properties) {
        if (null != properties) renderProperties.putAll(properties);
    }

    protected TemplateBase internalClone() {
        try {
            return (TemplateBase)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public ITemplate cloneMe(RythmEngine engine, ITemplate caller) {
        if (null == engine) throw new NullPointerException();
        TemplateBase tmpl = internalClone();
        if (tmpl.__parent != null) {
            tmpl.__parent = (TemplateBase) tmpl.__parent.cloneMe(engine, caller);
        }
        tmpl.engine = engine;
        tmpl.templateClass = templateClass;
        if (null != caller) {
            tmpl._caller = (TextBuilder)caller;
        }
        tmpl.__ctx = new Context(__ctx);
        //if (null != out) tmpl._out = out;
        if (null != _out) tmpl._out = new StringBuilder();
        tmpl._properties = new HashMap<String, Object>(_properties.size());
        tmpl.layoutContent = "";
        tmpl.layoutSections = new HashMap<String, String>();
        tmpl.renderProperties = new HashMap<String, Object>();
        tmpl.section = null;
        tmpl.tmpCaller = null;
        tmpl.tmpOut = null;
        tmpl._logTime = _logTime;
        return tmpl;
    }

    protected void internalInit() {
        loadExtendingArgs();
        init();
    }

    protected void setup() {
    }

    protected void loadExtendingArgs() {
    }

    @Override
    public void init() {
    }

    private boolean _logTime() {
        return _logTime || engine.logRenderTime;
    }

    public TemplateClass getTemplateClass(boolean useCaller) {
        TemplateClass tc = templateClass;
        if (useCaller && null == tc) {
            TemplateBase caller = caller();
            if (null != caller) return caller.getTemplateClass(true);
        }
        return tc;
    }

    @Override
    public final String render() {
        try {
            long l = 0l;
            if (_logTime()) {
                l = System.currentTimeMillis();
            }
            engine.preprocess(this);
            setup();
            if (_logTime()) {
                _logger.debug("< preprocess [%s]: %sms", getClass().getName(), System.currentTimeMillis() - l);
                l = System.currentTimeMillis();
            }
            String s = internalRender();
            if (_logTime()) {
                _logger.debug("<<<<<<<<<<<< [%s] total render: %sms", getClass().getName(), System.currentTimeMillis() - l);
            }
            return s;
        } catch (ClassReloadException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Cannot hotswap class, try to restart engine...");
            }
            engine.restart(e);
            return render();
        } catch (ClassCastException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ClassCastException found, force refresh template and try again...");
            }
            TemplateClass tc = engine.classes.getByClassName(getClass().getName());
            tc.refresh(true);
            ITemplate t = tc.asTemplate();
            return t.render();
        }
    }

    private Writer w_ = null;
    protected void _setOutput(String path) {
        try {
            w_ = new BufferedWriter(new FileWriter(path));
        } catch (Exception e) {
            throw new FastRuntimeException(e.getMessage());
        }
    }
    protected void _setOutput(File file) {
        try {
            w_ = new BufferedWriter(new FileWriter(file));
        } catch (Exception e) {
            throw new FastRuntimeException(e.getMessage());
        }
    }
    protected void _setOutput(OutputStream os) {
        w_ = new OutputStreamWriter(os);
    }
    protected void _setOutput(Writer w) {
        w_ = w;
    }

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
                        _logger.debug("<<<<<<<<<<<< [%s] build: %sms", getClass().getName(), System.currentTimeMillis() - l);
                    }
                } catch (RythmException e) {
                    throw e;
                } catch (Exception e) {
                    StackTraceElement[] stackTrace = e.getStackTrace();
                    String msg = null;
                    for (StackTraceElement se : stackTrace){
                        String cName = se.getClassName();
                        if (cName.contains(TemplateClass.CN_SUFFIX)) {
                            // is it the embedded class?
                            if (cName.indexOf("$") != -1) {
                                cName = cName.substring(0, cName.lastIndexOf("$"));
                            }
                            TemplateClass tc = engine.classes.getByClassName(cName);
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
                            RythmException re = new RythmException(engine, e, tc, se.getLineNumber(), -1, msg);
                            if (engine.logSourceInfoOnRuntimeError) {
                                Logger.error("Error executing template: %2$s. \n%1$s\n%2$s", re.javaSourceInfo(), re.templateSourceInfo(), msg);
                            }
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
                    throw (e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e));
                }
            } catch (RuntimeException e) {
                // try to restart engine
                try {
                    engine.restart(e);
                } catch (RuntimeException e0) {
                    // ignore it because we already thrown it out
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

    protected String internalRender() {
        internalBuild();
        if (null != __parent) {
            __parent.setLayoutContent(toString());
            __parent.addAllLayoutSections(layoutSections);
            __parent.addAllRenderProperties(renderProperties);
            __parent._properties.putAll(_properties);
            return __parent.render();
        } else {
            return toString();
        }
    }

    public TextBuilder build() {
        return this;
    }

    @Override
    public void setRenderArgs(Map<String, Object> args) {
        _properties.putAll(args);
    }

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
        _properties.put(name, arg);
    }

    protected final void _set(String name, Object arg) {
        setRenderArg(name, arg);
    }

    protected final TemplateBase caller() {
        return null == _caller ? null : (TemplateBase)_caller;
    }

    @Override
    public <T> T getRenderArg(String name) {
        Object val = _properties.get(name);
        return (T)(null != val ? val : (null != _caller ? caller().getRenderArg(name) : null));
    }

    protected final <T> T _get(String name) {
        return getRenderArg(name);
    }

    protected final <T> T _getAs(String name, Class<T> c) {
        Object o = getRenderArg(name);
        if (null == o) return null;
        return (T)o;
    }

    protected final <T> T _getRenderProperty(String name, Object def) {
        Object o = renderProperties.get(name);
        return (T)(null == o ? def : o);
    }

    protected final <T> T _getRenderProperty(String name) {
        return (T)_getRenderProperty(name, null);
    }

    protected final <T> T _getRenderPropertyAs(String name, T def) {
        Object o = _getRenderProperty(name, def);
        return null == o ? def : (T)o;
    }

    protected final void _setRenderProperty(String name, Object val) {
        renderProperties.put(name, val);
    }

    protected final void handleTemplateExecutionException(Exception e) {
        try {
            _engine().handleTemplateExecutionException(e, this);
        } catch (RuntimeException e0) {
            throw e0;
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public Map<String, Object> getRenderArgs() {
        return new HashMap<String, Object>(_properties);
    }

    @Override
    public void setRenderArg(int position, Object arg) {
    }

    public Context __ctx = new Context();

    public final TemplateBase pe(Object o) {
        if (null == o) return this;
        if (o instanceof ITemplate.RawData) {
            return (TemplateBase)p(o);
        }
        ITemplate.Escape escape = __ctx.currentEscape();
        return pe(o, escape);
    }

    public final TemplateBase pe(Object o, ITemplate.Escape escape) {
        if (null == o) return this;
        if (o instanceof ITemplate.RawData) {
            return (TemplateBase)p(o);
        }
        if (null == escape) escape = __ctx.currentEscape();
        return (TemplateBase)super.pe(o, escape);
    }
        // --- debugging interface
    protected static ILogger _logger = Logger.get(TemplateBase.class);
    protected static void _log(String msg, Object... args) {
        _logger.info(msg, args);
    }
    protected static void _debug(String msg, Object... args) {
        _logger.debug(msg, args);
    }
    protected static void _info(String msg, Object... args) {
        _logger.info(msg, args);
    }
    protected static void _warn(String msg, Object... args) {
        _logger.error(msg, args);
    }
    protected static void _warn(Throwable t, String msg, Object... args) {
        _logger.error(t, msg, args);
    }
    protected static void _error(String msg, Object... args) {
        _logger.error(msg, args);
    }
    protected static void _error(Throwable t, String msg, Object... args) {
        _logger.error(t, msg, args);
    }
    protected boolean _logTime = false;

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
                    return ((T[])_o)[cursor++];  //To change body of implemented methods use File | Settings | File Templates.
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
                    return (T)((Integer)((int[])_o)[cursor++]);
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
                    return (T)((Long)((long[])_o)[cursor++]);
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
                    return (T)((Float)((float[])_o)[cursor++]);
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
                    return (T)((Double)((double[])_o)[cursor++]);
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
                    return (T)((Short)((short[])_o)[cursor++]);
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
                    return (T)((Character)((char[])_o)[cursor++]);
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
                    return (T)((Byte)((byte[])_o)[cursor++]);
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
                    return (T)((Boolean)((boolean[])_o)[cursor++]);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public _Itr(Collection<T> tc) {
            _o = tc;
            _size = tc.size();
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
