package com.reversecoder.git.api;

public class ConnectionException extends GitApiException {

    public ConnectionException(final String message) {
        super(message);
    }

    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
