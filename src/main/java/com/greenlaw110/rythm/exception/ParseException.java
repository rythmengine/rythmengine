package com.greenlaw110.rythm.exception;

public class ParseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ParseException() {
    }

    public ParseException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }

}
