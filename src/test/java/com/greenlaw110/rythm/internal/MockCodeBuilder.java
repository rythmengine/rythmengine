package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.utils.S;

public class MockCodeBuilder extends CodeBuilder {

    public MockCodeBuilder(String template, String className, String tagName) {
        super(template, className, tagName, null, null);
    }

    public boolean hasImport(String importStr) {
        return imports.contains(importStr);  
    }
    
    public boolean hasRenderArg(String type, String name) {
        RenderArgDeclaration dec = renderArgs.get(name);
        if (null == dec) return false;
        if (!S.isEqual(dec.type, type)) return false;
        return true;
    }
}
