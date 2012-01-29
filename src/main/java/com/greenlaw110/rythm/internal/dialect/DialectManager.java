package com.greenlaw110.rythm.internal.dialect;

import com.greenlaw110.rythm.spi.IDialect;

public class DialectManager {
    IDialect def = null;
    public DialectManager() {
        def = new Rythm();
    }
    public IDialect get() {
        return def;
    }
    public IDialect get(String id) {
        if (null == id || "rythm".equalsIgnoreCase(id)) return def;
        return null;
    }
}
