package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.extension.ILang;
import com.greenlaw110.rythm.extension.IRenderExceptionHandler;
import com.greenlaw110.rythm.extension.ITagInvokeListener;
import com.greenlaw110.rythm.extension.Transformer;
import com.greenlaw110.rythm.utils.S;

import java.util.ArrayList;
import java.util.List;


public class ExtensionManager {

    private final List<IJavaExtension> _extensions = new ArrayList<IJavaExtension>();
    private final RythmEngine engine;

    public ExtensionManager(RythmEngine engine) {
        if (null == engine) throw new NullPointerException();
        this.engine = engine;
    }

    /**
     * Add a Java extension
     *
     * @param javaExtension
     */
    public void registerJavaExtension(IJavaExtension javaExtension) {
        _extensions.add(javaExtension);
    }

    Iterable<IJavaExtension> javaExtensions() {
        return _extensions;
    }

    /**
     * Is a specified method name a java extension?
     *
     * @param s
     * @return
     */
    public boolean isJavaExtension(String s) {
        for (IJavaExtension ext : _extensions) {
            if (S.isEqual(s, ext.methodName())) {
                return true;
            }
        }
        return false;
    }

    public ExtensionManager registerUserDefinedParsers(IParserFactory... parsers) {
        return registerUserDefinedParsers(null, parsers);
    }

    /**
     * Register a special case parser to a dialect
     * <p/>
     * <p>for example, the play-rythm plugin might want to register a special case parser to
     * process something like @{Controller.actionMethod()} or &{'MSG_ID'} etc to "japid"
     * and "play-groovy" dialects
     *
     * @param dialect
     * @param parsers
     */
    public ExtensionManager registerUserDefinedParsers(String dialect, IParserFactory... parsers) {
        engine.dialectManager().registerExternalParsers(dialect, parsers);
        return this;
    }

    private List<IRenderExceptionHandler> exceptionHandlers = new ArrayList<IRenderExceptionHandler>();

    public ExtensionManager registerTemplateExecutionExceptionHandler(IRenderExceptionHandler h) {
        if (!exceptionHandlers.contains(h)) exceptionHandlers.add(h);
        return this;
    }

    public Iterable<IRenderExceptionHandler> exceptionHandlers() {
        return exceptionHandlers;
    }

    private List<IExpressionProcessor> expressionProcessors = new ArrayList<IExpressionProcessor>();

    public ExtensionManager registerExpressionProcessor(IExpressionProcessor p) {
        if (!expressionProcessors.contains(p)) expressionProcessors.add(p);
        return this;
    }

    public Iterable<IExpressionProcessor> expressionProcessors() {
        return expressionProcessors;
    }

    private List<ITagInvokeListener> tagInvokeListeners = new ArrayList<ITagInvokeListener>();

    public ExtensionManager registerTagInvoeListener(ITagInvokeListener l) {
        if (!tagInvokeListeners.contains(l)) tagInvokeListeners.add(l);
        return this;
    }

    public Iterable<ITagInvokeListener> tagInvokeListeners() {
        return tagInvokeListeners;
    }

    private List<ILang> templateLangList = new ArrayList<ILang>();

    public ExtensionManager registerTemplateLang(ILang lang) {
        templateLangList.add(lang);
        return this;
    }

    public Iterable<ILang> templateLangs() {
        return templateLangList;
    }

    public boolean hasTemplateLangs() {
        return !templateLangList.isEmpty();
    }

    public ExtensionManager registerJavaExtensions(Class<? extends Transformer> c) {
        //TODO
        return this;
    }

}
