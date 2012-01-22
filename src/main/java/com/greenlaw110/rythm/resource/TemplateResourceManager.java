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
    
    public TemplateResourceManager(RythmEngine engine) {
        this.engine = engine;
    }
    
    private ITemplateResource cache(ITemplateResource resource) {
        if (resource.isValid()) cache.put(resource.getKey(), resource);
        return resource;
    }
    
    public ITemplateResource get(File file) {
        return cache(new FileTemplateResource(file, engine));
    }
    
    public ITemplateResource get(String str) {

        ITemplateResource resource = cache.get(str);
        if (null != resource) return resource;
        
        String loader = engine.configuration.getProperty("rythm.loader");
        if ("file".equalsIgnoreCase(loader)) {
            resource = new FileTemplateResource(str, engine);
            if (!resource.isValid()) resource = new ClasspathTemplateResource(str);
        } else {
            resource = new ClasspathTemplateResource(str, engine);
            if (!resource.isValid()) resource = new FileTemplateResource(str);
        }

        if (!resource.isValid()) resource = new StringTemplateResource(str, engine);
        return cache(resource);
    }
}
