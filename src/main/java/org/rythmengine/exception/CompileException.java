/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.exception;

import org.rythmengine.RythmEngine;
import org.rythmengine.internal.RythmEvents;
import org.rythmengine.internal.compiler.TemplateClass;
import org.rythmengine.internal.parser.build_in.ExpressionParser;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 14/02/12
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("serial")
public class CompileException extends RythmException {

    /**
     * a Compiler Exception
     *
     */
    public static class CompilerException extends RuntimeException {
        public String className;
        public int javaLineNumber;
        public String message;

        private CompilerException() {
        }
    }

    /**
     * create a compiler exception for the given className, line number and message
     * @param className
     * @param line
     * @param message
     * @return the CompilerException
     */
    public static CompilerException compilerException(String className, int line, String message) {
        CompilerException e = new CompilerException();
        e.javaLineNumber = line;
        e.message = ExpressionParser.reversePositionPlaceHolder(message);
        e.className = className;
        return e;
    }

    /**
     * construct a compile exception
     * @param engine
     * @param tc
     * @param javaLineNumber
     * @param message
     */
    public CompileException(RythmEngine engine, TemplateClass tc, int javaLineNumber, String message) {
        super(engine, tc, javaLineNumber, -1, message);
        RythmEvents.COMPILE_FAILED.trigger(engine, tc);
    }

    @Override
    public String errorTitle() {
        return "Rythm Compilation Error";
    }

    @Override
    public String errorDesc() {
        return String.format("The template[%s] cannot be compiled: <strong>%s</strong>", this.getTemplateName(), this.originalMessage.replace("<", "&lt;"));
    }
}
