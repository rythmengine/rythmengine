package com.greenlaw110.rythm.exception;

public class DialectNotSupportException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public DialectNotSupportException(String id) {
        super(String.format("Dialect[%s] not supported", id));
    }
    
}
