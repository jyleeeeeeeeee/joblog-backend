package com.joblog.common.exception;

public class JwtInvalid401Exception extends RuntimeException {
    public JwtInvalid401Exception(String message) {
        super(message);
    }
}
