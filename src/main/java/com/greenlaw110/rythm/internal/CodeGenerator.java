package com.greenlaw110.rythm.internal;

public class CodeGenerator {
    public String generate(String template, String className) {
        return new CodeBuilder(template, className).build().toString();
    }
}
