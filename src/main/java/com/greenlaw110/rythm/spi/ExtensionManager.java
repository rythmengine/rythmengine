package com.greenlaw110.rythm.spi;

import com.greenlaw110.rythm.ILang;
import com.greenlaw110.rythm.ITagInvokeListener;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;

import java.util.ArrayList;
import java.util.List;


public class ExtensionManager {

    private RythmEngine engine;

    public ExtensionManager(RythmEngine engine) {
        this.engine = engine;
    }

    RythmEngine engine() {
        return null == engine ? Rythm.engine() : engine;
    }

    public ExtensionManager registerUserDefinedParsers(IParserFactory... parsers) {
        return registerUserDefinedParsers(null, parsers);
    }

    /**
     * Register a special case parser to a dialect
     *
     * <p>for example, the play-rythm plugin might want to register a special case parser to
     * process something like @{Controller.actionMethod()} or &{'MSG_ID'} etc to "japid"
     * and "play-groovy" dialects
     *
     * @param dialect
     * @param parsers
     */
    public ExtensionManager registerUserDefinedParsers(String dialect, IParserFactory... parsers) {
        engine().getDialectManager().registerExternalParsers(dialect, parsers);
        return this;
    }

    private List<ITemplateExecutionExceptionHandler> exceptionHandlers = new ArrayList<ITemplateExecutionExceptionHandler>();
    public ExtensionManager registerTemplateExecutionExceptionHandler(ITemplateExecutionExceptionHandler h) {
        if (!exceptionHandlers.contains(h)) exceptionHandlers.add(h);
        return this;
    }

    public Iterable<ITemplateExecutionExceptionHandler> exceptionHandlers() {
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

    private List<ITemplatePreProcessor> preprocessors = new ArrayList<ITemplatePreProcessor>();
    public ExtensionManager registerPreprocessor(ITemplatePreProcessor p) {
        preprocessors.add(p);
        return this;
    }
    
    public Iterable<ITemplatePreProcessor> preProcessors() {
        return preprocessors;
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
    
    public ExtensionManager registerJavaExtensions(Class<? extends JavaExtension> c){
        //TODO
        return this;
    }

}
