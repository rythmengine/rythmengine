package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.F;

/**
 * Built in {@link IEvent event}s
 */
public class RythmEvents<PARAM> implements IEvent<PARAM> {
    /**
     * Right before template parsing started
     */
    public static final 
    IEvent<CodeBuilder> ON_PARSE = new RythmEvents<CodeBuilder>();

    /**
     * Before start building java source code. A good place to inject implicit
     * imports and render args
     */
    public static final
    IEvent<CodeBuilder> ON_BUILD_JAVA_SOURCE = new RythmEvents<CodeBuilder>();

    /**
     * Before close generated java source code class. A good place to inject
     * implicit java source
     */
    public static final
    IEvent<CodeBuilder> ON_CLOSING_JAVA_CLASS = new RythmEvents<CodeBuilder>();

    /**
     * Immediately after template get parsed and before final template java
     * source code generated
     */
    public static final 
    IEvent<CodeBuilder> PARSED = new RythmEvents<CodeBuilder>();

    /**
     * Right before template compilation started
     */
    public static final 
    IEvent<String> ON_COMPILE = new RythmEvents<String>();

    /**
     * Immediately after template compilation finished and before get cached on disk
     */
    public static final 
    IEvent<String> COMPILED = new RythmEvents<String>();

    /**
     * Before template render start. A good place to set implicit render args
     */
    public static final 
    IEvent<ITemplate> ON_RENDER = new RythmEvents<ITemplate>();

    /**
     * After template rendered.
     */
    public static final 
    IEvent<ITemplate> RENDERED = new RythmEvents<ITemplate>();

    /**
     * Before tag invocation
     */
    public static final 
    IEvent<F.T2<TemplateBase, ITag>> ON_TAG_INVOCATION = new RythmEvents<F.T2<TemplateBase, ITag>>();

    /**
     * Before tag invocation
     */
    public static final 
    IEvent<F.T2<TemplateBase, ITag>> TAG_INVOKED = new RythmEvents<F.T2<TemplateBase, ITag>>();
    
    /**
     * Render execution exception captured
     */
    public static final 
    IEvent<F.T2<TemplateBase, Exception>> ON_RENDER_EXCEPTION = new RythmEvents<F.T2<TemplateBase, Exception>>();

    @Override
    public void trigger(IEventDispatcher eventBus, PARAM eventParam) {
        eventBus.accept(this, eventParam);
    }
}
