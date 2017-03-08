package com.reversecoder.git.api;

public class ResourceDoesNotExistException extends GitApiException {

    public ResourceDoesNotExistException(final String message) {
        super(message);
    }

    public ResourceDoesNotExistException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
