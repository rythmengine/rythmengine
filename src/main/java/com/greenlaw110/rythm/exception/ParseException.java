package com.greenlaw110.rythm.exception;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;

public class ParseException extends RythmException {

    public ParseException(RythmEngine engine, TemplateClass tc, int line, String message, Object... args) {
        this(engine, null, tc, line, message, args);
    }

    public ParseException(RythmEngine engine, Throwable cause, TemplateClass tc, int line, String message, Object... args) {
        super(engine, cause, tc, -1, line, String.format(message, args));
    }

}
