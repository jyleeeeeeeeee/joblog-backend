package com.joblog.common.exception;

public abstract class JwtInvalidException extends RuntimeException{
    public JwtInvalidException(String message) {
        super(message);
    }
}
