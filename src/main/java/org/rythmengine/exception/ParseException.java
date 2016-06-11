/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.exception;

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.RythmEvents;
import org.rythmengine.internal.compiler.TemplateClass;

public class ParseException extends RythmException {

    public ParseException(RythmEngine engine, TemplateClass tc, int line, String message, Object... args) {
        this(engine, null, tc, line, message, args);
    }

    public ParseException(RythmEngine engine, Throwable cause, TemplateClass tc, int line, String message, Object... args) {
        super(engine, cause, tc, -1, line, String.format(message, args));
        RythmEvents.PARSE_FAILED.trigger(engine, tc);
    }

    @Override
    public String errorTitle() {
        return "Rythm parse error";
    }

    @Override
    public String errorDesc() {
        return String.format("The template[%s] cannot be parsed: <strong>%s</strong>", getTemplateName(), originalMessage.replace("<", "&lt;"));
    }
}
