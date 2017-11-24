package com.github.cafune1853.mybatis.support.exception;

public class ReflectionException extends RuntimeException {
    public ReflectionException() {
    }

    public ReflectionException(String msg) {
        super(msg);
    }

    public ReflectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
