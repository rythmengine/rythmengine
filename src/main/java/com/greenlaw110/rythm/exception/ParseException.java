package com.greenlaw110.rythm.exception;

import com.greenlaw110.rythm.internal.compiler.TemplateClass;

public class ParseException extends RythmException {

    public ParseException(TemplateClass tc, int line, String message, Object... args) {
        this(null, tc, line, message, args);
    }

    public ParseException(Throwable cause, TemplateClass tc, int line, String message, Object... args) {
        super(cause, tc, -1, line, String.format(message, args));
        errorMessage = String.format("Error parsing template[%s], line: %s, error: %s", tc.name(), line, originalMessage);
    }

}
