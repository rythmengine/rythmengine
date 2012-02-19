package com.greenlaw110.rythm.exception;

import com.greenlaw110.rythm.internal.compiler.TemplateClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 14/02/12
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompileException extends RythmException {

    public static class CompilerException extends RuntimeException {
        public String className;
        public int javaLineNumber;
        public String message;
        private CompilerException(){}
    }
    
    public static CompilerException compilerException(String className, int line, String message) {
        CompilerException e = new CompilerException();
        e.javaLineNumber = line;
        e.message = message;
        e.className = className;
        return e;
    }

    public CompileException(TemplateClass tc, int javaLineNumber, String message) {
        super(tc, javaLineNumber, -1, message);
        errorMessage = String.format("Error compile template[%s]. Java code line: %s; Template code line:%s; Error: %s", tc.getKey(), javaLineNumber, templatelineNumber, originalMessage);
    }
    
}
