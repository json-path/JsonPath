package com.jayway.jsonpath.internal;

/**
 * An alternative to {@link com.jayway.jsonpath.PathNotFoundException} but without stacktrace.
 * Throwing an exception can be very expensive because of {@link Throwable#fillInStackTrace()}.
 * This can be a performance issue. It is pointless to generate stacktrace when
 * {@link com.jayway.jsonpath.Option#SUPPRESS_EXCEPTIONS} is active.
 */
public class FastPathNotFoundException extends RuntimeException {

    public FastPathNotFoundException(String message) {
        super(message);
    }

    public FastPathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastPathNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
