package com.greenlaw110.rythm.template;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.exception.RythmException;
import com.greenlaw110.rythm.internal.compiler.ClassReloadException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.ILogger;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public abstract class TemplateBase extends TextBuilder implements ITemplate {

    protected transient RythmEngine engine = null;

    protected Map<String, Object> _properties = new HashMap<String, Object>();

    protected RythmEngine _engine() {
        return null == engine ? Rythm.engine : engine;
    }

    protected void _invokeTag(String name) {
        _engine().invokeTag(name, this, null, null);
    }

    protected void _invokeTag(String name, ITag.ParameterList params) {
        _engine().invokeTag(name, this, params, null);
    }

    protected void _invokeTag(String name, ITag.ParameterList params, ITag.Body body) {
        _engine().invokeTag(name, this, params, body);
    }

    /* to be used by dynamic generated sub classes */
    private String renderBody = "";
    private Map<String, String> renderSections = new HashMap<String, String>();
    private Map<String, Object> renderProperties = new HashMap<String, Object>();

    protected TemplateBase __parent = null;

    public TemplateBase() {
        super();
        Class<? extends TemplateBase> c = getClass();
        Class<?> pc = c.getSuperclass();
        if (TemplateBase.class.isAssignableFrom(pc) && !Modifier.isAbstract(pc.getModifiers())) {
            try {
                __parent = (TemplateBase) pc.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected final void setRenderBody(String body) {
        renderBody = body;
    }

    private void addRenderSection(String name, String section) {
        renderSections.put(name, section);
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
        if (null == tmpOut && null == tmpCaller) throw new IllegalStateException("section has not been started");
        addRenderSection(section, _out.toString());
        _out = tmpOut;
        _caller = tmpCaller;
        tmpOut = null;
        tmpCaller = null;
    }

    protected void _pSection(String name) {
        p(renderSections.get(name));
    }

    protected void _pBody() {
        p(renderBody);
    }

    private void addAllRenderSections(Map<String, String> sections) {
        if (null != sections) renderSections.putAll(sections);
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
        //if (null != out) tmpl._out = out;
        if (null != caller) {
            tmpl._caller = (TextBuilder)caller;
        }
        if (null != _out) tmpl._out = new StringBuilder();
        return tmpl;
    }

    protected void internalInit() {
        loadExtendingArgs();
        init();
    }

    protected void loadExtendingArgs() {
    }

    @Override
    public void init() {
    }

    private static final Pattern P = Pattern.compile(".*\\/\\/line:\\s*([0-9]+).*");
    @Override
    public final String render() {
        try {
            engine.preprocess(this);
            return internalRender();
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

    protected void internalBuild() {
        internalInit();

        if (engine.isProdMode()) {
            build();
        } else {
            RuntimeException theRE = null;
            try {
                build();
            } catch (RythmException e) {
                theRE = e;
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
                                msg = "caused by " + e.getClass().getName();
                                //System.out.println("<<<<<" + msg);
                            }
                        }
                        RythmException re = new RythmException(e, tc, se.getLineNumber(), -1, msg);
                        if (re.templatelineNumber != -1) {
                            StackTraceElement[] newStack = new StackTraceElement[stackTrace.length + 1];
                            newStack[0] = new StackTraceElement(tc.name(), "", tc.getKey(), re.templatelineNumber);
                            System.arraycopy(stackTrace, 0, newStack, 1, stackTrace.length);
                            re.setStackTrace(newStack);
                        }
                        theRE = re;
                        throw re;
                    }
                }
                theRE = (e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e));
                throw theRE;
            } finally {
                // try to restart engine
                try {
                    engine.restart(theRE);
                } catch (RuntimeException e) {
                    // ignore it because we already thrown it out
                }
            }
        }
    }

    protected String internalRender() {
        internalBuild();
        if (null != __parent) {
            __parent.setRenderBody(toString());
            __parent.addAllRenderSections(renderSections);
            __parent.addAllRenderProperties(renderProperties);
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
    public Object getRenderArg(String name) {
        Object val = _properties.get(name);
        return null != val ? val : (null != _caller ? caller().getRenderArg(name) : null);
    }

    protected final Object _get(String name) {
        return getRenderArg(name);
    }

    protected final <T> T _getAs(String name, Class<T> c) {
        Object o = getRenderArg(name);
        if (null == o) return null;
        return (T)o;
    }

    protected final Object _getRenderProperty(String name, Object def) {
        Object o = renderProperties.get(name);
        return null == o ? def : o;
    }

    protected final Object _getRenderProperty(String name) {
        return _getRenderProperty(name, null);
    }

    protected final <T> T _getRenderPropertyAs(String name, T def) {
        Object o = _getRenderProperty(name, def);
        return null == o ? def : (T)o;
    }

    protected final void _setRenderProperty(String name, Object val) {
        renderProperties.put(name, val);
    }

    @Override
    public Map<String, Object> getRenderArgs() {
        return new HashMap<String, Object>(_properties);
    }

    @Override
    public void setRenderArg(int position, Object arg) {
    }

    @Override
    public StringBuilder getOut() {
        return out();
    }

    @Override
    public void setOut(StringBuilder out) {
        if (null != _caller) ((ITemplate)_caller).setOut(out);
        else _out = out;
    }

    // --- debugging interface
    protected static ILogger _logger = Logger.get(TemplateBase.class);
    protected static void _debug(String msg, Object... args) {
        _logger.debug(msg, args);
    }
    protected static void _info(String msg, Object... args) {
        _logger.info(msg, args);
    }
    protected static void _error(String msg, Object... args) {
        _logger.error(msg, args);
    }
    protected static void _error(Throwable t, String msg, Object... args) {
        _logger.error(t, msg, args);
    }
}
