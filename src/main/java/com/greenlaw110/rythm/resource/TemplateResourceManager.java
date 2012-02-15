package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.RythmEngine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateResourceManager {
    
    private RythmEngine engine;
    
    private Map<String, ITemplateResource> cache = new HashMap<String, ITemplateResource>();

    public ITemplateResourceLoader resourceLoader = null;

    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
    }
    
    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) cache.put(resource.getKey(), resource);
        return resource;
    }
    
    public void tryLoadTag(String tagName) {
        if (null != resourceLoader) resourceLoader.tryLoadTag(tagName);
        else FileTemplateResource.tryLoadTag(tagName, engine);
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
