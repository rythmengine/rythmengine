package com.greenlaw110.rythm.exception;

public class DialectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DialectNotFoundException(String id) {
        super(String.format("Dialect[%s] not found", id));
    }
    
}
