package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.template.ITag;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.template.TemplateBase;
import com.greenlaw110.rythm.utils.F;

/**
 * Built in {@link IEvent event}s
 */
public class RythmEvents<RETURN, PARAM> implements IEvent<RETURN, PARAM> {
    /**
     * Right before template parsing started
     */
    public static final
    IEvent<Void, CodeBuilder> ON_PARSE = new RythmEvents<Void, CodeBuilder>();

    /**
     * Before start building java source code. A good place to inject implicit
     * imports and render args
     */
    public static final
    IEvent<Void, CodeBuilder> ON_BUILD_JAVA_SOURCE = new RythmEvents<Void, CodeBuilder>();

    /**
     * Before close generated java source code class. A good place to inject
     * implicit java source
     */
    public static final
    IEvent<Void, CodeBuilder> ON_CLOSING_JAVA_CLASS = new RythmEvents<Void, CodeBuilder>();

    /**
     * Immediately after template get parsed and before final template java
     * source code generated
     */
    public static final
    IEvent<Void, CodeBuilder> PARSED = new RythmEvents<Void, CodeBuilder>();

    /**
     * Right before template compilation started
     */
    public static final
    IEvent<Void, String> ON_COMPILE = new RythmEvents<Void, String>();

    /**
     * Immediately after template compilation finished and before get cached on disk
     */
    public static final
    IEvent<Void, String> COMPILED = new RythmEvents<Void, String>();

    /**
     * Before template render start. A good place to set implicit render args
     */
    public static final
    IEvent<Void, ITemplate> ON_RENDER = new RythmEvents<Void, ITemplate>();

    /**
     * After template rendered.
     */
    public static final
    IEvent<Void, ITemplate> RENDERED = new RythmEvents<Void, ITemplate>();

    /**
     * Before tag invocation
     */
    public static final
    IEvent<Void, F.T2<TemplateBase, ITag>> ON_TAG_INVOCATION = new RythmEvents<Void, F.T2<TemplateBase, ITag>>();

    /**
     * Before tag invocation
     */
    public static final
    IEvent<Void, F.T2<TemplateBase, ITag>> TAG_INVOKED = new RythmEvents<Void, F.T2<TemplateBase, ITag>>();

    /**
     * Render execution exception captured
     */
    public static final
    IEvent<Boolean, F.T2<TemplateBase, Exception>> ON_RENDER_EXCEPTION = new RythmEvents<Boolean, F.T2<TemplateBase, Exception>>();

    @Override
    public RETURN trigger(IEventDispatcher eventBus, PARAM eventParam) {
        return (RETURN) eventBus.accept(this, eventParam);
    }
}
