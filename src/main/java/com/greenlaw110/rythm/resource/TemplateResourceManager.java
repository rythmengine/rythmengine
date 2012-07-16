package com.greenlaw110.rythm.resource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateResourceManager {

    private RythmEngine engine;

    private Map<Object, ITemplateResource> cache = new HashMap<Object, ITemplateResource>();

    public ITemplateResourceLoader resourceLoader = null;

    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
    }

    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) cache.put(resource.getKey(), resource);
        return resource;
    }

    public TemplateClass tryLoadTag(String tagName, TemplateClass tc) {
        if (null != resourceLoader) return resourceLoader.tryLoadTag(tagName, tc);
        else return FileTemplateResource.tryLoadTag(tagName, engine);
    }

    public String getFullTagName(TemplateClass tc) {
        if (null != resourceLoader) return resourceLoader.getFullTagName(tc);
        else return FileTemplateResource.getFullTagName(tc, engine);
    }

    public ITemplateResource get(File file) {
        return cache(new FileTemplateResource(file, engine));
    }

    public ITemplateResource get(String str) {
        ITemplateResource resource = getFileResource(str);
        if (!resource.isValid()) resource = new StringTemplateResource(str, engine);
        return cache(resource);
    }

    public ITemplateResource getFileResource(String str) {
        ITemplateResource resource = cache.get(str);
        if (null != resource) return resource;

        if (null != resourceLoader) resource = resourceLoader.load(str);
        if (null != resource) return resource;

        // try build-in loader
        resource = new FileTemplateResource(str, engine);
        if (!resource.isValid()) resource = new ClasspathTemplateResource(str);
        return cache(resource);
    }
}
