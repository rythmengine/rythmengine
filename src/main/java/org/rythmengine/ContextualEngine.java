package org.rythmengine;

import org.rythmengine.conf.RythmConfiguration;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.extension.IFormatter;
import org.rythmengine.extension.IPropertyAccessor;
import org.rythmengine.extension.ITemplateResourceLoader;
import org.rythmengine.internal.ExtensionManager;
import org.rythmengine.internal.IEvent;
import org.rythmengine.internal.IEventDispatcher;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.internal.compiler.TemplateClassCache;
import org.rythmengine.internal.compiler.TemplateClassLoader;
import org.rythmengine.internal.compiler.TemplateClassManager;
import org.rythmengine.internal.dialect.DialectManager;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.TemplateResourceManager;
import org.rythmengine.template.ITag;
import org.rythmengine.template.ITemplate;
import org.rythmengine.template.JavaTagBase;
import org.rythmengine.toString.ToStringOption;
import org.rythmengine.toString.ToStringStyle;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * A contextual engine composed of a delegated engine plus runtime settings
 */
class ContextualEngine extends RythmEngine {
    private RythmEngine e;
    private RenderSettings settings;

    ContextualEngine(RythmEngine engine, RenderSettings settings) {
        super(true);
        this.e = engine;
        this.settings = settings;
    }

    @Override
    public RenderSettings renderSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return e.toString();
    }

    @Override
    public RythmConfiguration conf() {
        return e.conf();
    }

    @Override
    public String version() {
        return e.version();
    }

    @Override
    public Rythm.Mode mode() {
        return e.mode();
    }

    @Override
    public String id() {
        return e.id();
    }

    @Override
    public String getId() {
        return e.getId();
    }

    @Override
    public boolean isSingleton() {
        return e.isSingleton();
    }

    @Override
    public boolean isProdMode() {
        return e.isProdMode();
    }

    @Override
    public boolean isDevMode() {
        return e.isDevMode();
    }

    @Override
    public TemplateResourceManager resourceManager() {
        return e.resourceManager();
    }

    @Override
    public TemplateClassManager classes() {
        return e.classes();
    }

    @Override
    public TemplateClassLoader classLoader() {
        return e.classLoader();
    }

    @Override
    public TemplateClassCache classCache() {
        return e.classCache();
    }

    @Override
    public ExtensionManager extensionManager() {
        return e.extensionManager();
    }

    @Override
    public DialectManager dialectManager() {
        return e.dialectManager();
    }

    public ContextualEngine() {
        super();
    }

    public ContextualEngine(File file) {
        super(file);
    }

    public ContextualEngine(Properties userConfiguration) {
        super(userConfiguration);
    }

    public ContextualEngine(Map<String, ?> userConfiguration) {
        super(userConfiguration);
    }

    @Override
    public void setProperty(String key, Object val) {
        super.setProperty(key, val);
    }

    @Override
    public <T> T getProperty(String key) {
        return e.getProperty(key);
    }

    @Override
    public void registerFormatter(Class<IFormatter>... formatterClasses) {
        super.registerFormatter(formatterClasses);
    }

    @Override
    public void registerFormatter(IFormatter... formatters) {
        super.registerFormatter(formatters);
    }

    @Override
    public void registerTransformer(Class<?>... transformerClasses) {
        super.registerTransformer(transformerClasses);
    }

    @Override
    public void registerTransformer(String namespace, String waivePattern, Class<?>... transformerClasses) {
        super.registerTransformer(namespace, waivePattern, transformerClasses);
    }

    @Override
    public void registerPropertyAccessor(IPropertyAccessor... accessors) {
        super.registerPropertyAccessor(accessors);
    }

    @Override
    public void registerResourceLoader(ITemplateResourceLoader... loaders) {
        super.registerResourceLoader(loaders);
    }

    @Override
    public ITemplate getTemplate(String template, Object... args) {
        return e.getTemplate(template, args);
    }

    @Override
    public TemplateClass getTemplateClass(ITemplateResource resource) {
        return e.getTemplateClass(resource);
    }

    @Override
    public ITemplate getTemplate(File file, Object... args) {
        return e.getTemplate(file, args);
    }

    @Override
    public String render(String template, Object... args) {
        return e.render(template, args);
    }

    @Override
    public void render(OutputStream os, String template, Object... args) {
        super.render(os, template, args);
    }

    @Override
    public void render(Writer w, String template, Object... args) {
        super.render(w, template, args);
    }

    @Override
    public String render(File file, Object... args) {
        return e.render(file, args);
    }

    @Override
    public void render(OutputStream os, File file, Object... args) {
        super.render(os, file, args);
    }

    @Override
    public void render(Writer w, File file, Object... args) {
        super.render(w, file, args);
    }

    @Override
    public String renderStr(String template, Object... args) {
        return e.renderStr(template, args);
    }

    @Override
    public String renderString(String template, Object... args) {
        return e.renderString(template, args);
    }

    @Override
    public String substitute(String template, Object... args) {
        return e.substitute(template, args);
    }

    @Override
    public String substitute(File file, Object... args) {
        return e.substitute(file, args);
    }

    @Override
    public String toString(String template, Object obj) {
        return e.toString(template, obj);
    }

    @Override
    public String toString(Object obj) {
        return e.toString(obj);
    }

    @Override
    public String toString(Object obj, ToStringOption option, ToStringStyle style) {
        return e.toString(obj, option, style);
    }

    @Override
    public String commonsToString(Object obj, ToStringOption option, org.apache.commons.lang3.builder.ToStringStyle style) {
        return e.commonsToString(obj, option, style);
    }

    @Override
    public String renderIfTemplateExists(String template, Object... args) {
        return e.renderIfTemplateExists(template, args);
    }

    @Override
    public Object eval(String script) {
        return e.eval(script);
    }

    @Override
    public Object eval(String script, Map<String, Object> params) {
        return e.eval(script, params);
    }

    @Override
    public Object eval(String script, Object context, Map<String, Object> params) {
        return e.eval(script, context, params);
    }

    @Override
    public boolean templateRegistered(String tmplName) {
        return e.templateRegistered(tmplName);
    }

    @Override
    public ITemplate getRegisteredTemplate(String tmplName) {
        return e.getRegisteredTemplate(tmplName);
    }

    @Override
    public TemplateClass getRegisteredTemplateClass(String name) {
        return e.getRegisteredTemplateClass(name);
    }

    @Override
    public RythmEngine registerTemplateClass(TemplateClass tc) {
        return e.registerTemplateClass(tc);
    }

    @Override
    public String testTemplate(String name, TemplateClass callerClass, ICodeType codeType) {
        return e.testTemplate(name, callerClass, codeType);
    }

    @Override
    public void registerFastTag(JavaTagBase tag) {
        super.registerFastTag(tag);
    }

    @Override
    public void registerTemplate(ITemplate template) {
        super.registerTemplate(template);
    }

    @Override
    public void registerTemplate(String name, ITemplate template) {
        super.registerTemplate(name, template);
    }

    @Override
    public void invokeTemplate(int line, String name, ITemplate caller, ITag.__ParameterList params, ITag.__Body body, ITag.__Body context) {
        super.invokeTemplate(line, name, caller, params, body, context);
    }

    @Override
    public void invokeTemplate(int line, String name, ITemplate caller, ITag.__ParameterList params, ITag.__Body body, ITag.__Body context, boolean ignoreNonExistsTag) {
        super.invokeTemplate(line, name, caller, params, body, context, ignoreNonExistsTag);
    }

    @Override
    public void cache(String key, Object o, int ttl, Object... args) {
        super.cache(key, o, ttl, args);
    }

    @Override
    public void cache(String key, Object o, String duration, Object... args) {
        super.cache(key, o, duration, args);
    }

    @Override
    public void evict(String key) {
        super.evict(key);
    }

    @Override
    public Serializable cached(String key, Object... args) {
        return e.cached(key, args);
    }

    @Override
    public void addExtendRelationship(TemplateClass parent, TemplateClass child) {
        super.addExtendRelationship(parent, child);
    }

    @Override
    public void invalidate(TemplateClass parent) {
        super.invalidate(parent);
    }

    @Override
    public synchronized Sandbox sandbox() {
        return e.sandbox();
    }

    @Override
    public Sandbox sandbox(Map<String, Object> context) {
        return e.sandbox(context);
    }

    @Override
    public IEventDispatcher eventDispatcher() {
        return e.eventDispatcher();
    }

    @Override
    public Object accept(IEvent event, Object param) {
        return e.accept(event, param);
    }

    @Override
    public void restart(RuntimeException cause) {
        super.restart(cause);
    }

    @Override
    void setShutdownListener(IShutdownListener listener) {
        super.setShutdownListener(listener);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
