package com.greenlaw110.rythm.cache;

import java.util.HashMap;
import java.util.Map;

import com.greenlaw110.rythm.internal.TemplateCompiler.CompiledTemplate;
import com.greenlaw110.rythm.template.ITemplate;

public class HashMapCache implements ICache {

    Map<String, CompiledTemplate> cache = new HashMap<String, CompiledTemplate>();

    @Override
    public void set(String key, CompiledTemplate compiledTemplate) {
        cache.put(key, compiledTemplate);
    }

    @Override
    public CompiledTemplate get(String key) {
        return cache.get(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void clear(String key) {
        cache.remove(key);
    }

}
