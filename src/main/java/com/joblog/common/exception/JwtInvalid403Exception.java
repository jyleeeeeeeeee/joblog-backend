package com.joblog.common.exception;

public class JwtInvalid403Exception extends RuntimeException {
    public JwtInvalid403Exception(String message) {
        super(message);
    }
}
