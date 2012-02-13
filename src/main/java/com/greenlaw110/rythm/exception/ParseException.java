package com.greenlaw110.rythm.exception;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private int line;
    
    public int getLine() {
        return line;
    }
    
    private String templateName;
    
    public String getTemplateName() {
        return templateName;
    }

    public ParseException(String templateName, int line, String message, Object... args) {
        super(format(templateName, line, message, args));
        this.line = line;
    }

    public ParseException(Throwable cause, int line) {
        super(cause);
        this.line = line;
    }

    public ParseException(Throwable cause, String templateName, int line, String message, Object... args) {
        super(format(templateName, line, message, args), cause);
        this.line = line;
    }

    private static String format(String templateName, int line, String message, Object... args) {
        message = String.format(message, args);
        return String.format("%s, line: %s, template: %s", message, line, templateName);
    }
}
