package com.greenlaw110.rythm.cache;

import com.greenlaw110.rythm.internal.TemplateCompiler.CompiledTemplate;

public interface ICache {
    void set(String key, CompiledTemplate ct);
    public CompiledTemplate get(String key);
    void clear();
    void clear(String key);
}
