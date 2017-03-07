package com.reversecoder.git.api;

public class ResourceDoesNotExistException extends GitApiException {

    /**
     * @param message
     */
    public ResourceDoesNotExistException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ResourceDoesNotExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
