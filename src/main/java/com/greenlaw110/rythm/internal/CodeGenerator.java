package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.RythmEngine;

public class CodeGenerator {

    private RythmEngine engine;

    public CodeGenerator(RythmEngine engine) {
        this.engine = engine;
    }

    public String generate(String template, String className, String tagName) {
        return new CodeBuilder(template, className, tagName, engine).build().toString();
    }
}
