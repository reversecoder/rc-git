package com.reversecoder.git.api;

public class UnsupportedProtocolException extends GitApiException {

    public UnsupportedProtocolException(final String message) {
        super(message);
    }

    public UnsupportedProtocolException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
