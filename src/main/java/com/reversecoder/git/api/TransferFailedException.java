package com.reversecoder.git.api;

public class TransferFailedException extends GitApiException {

    public TransferFailedException(final String message) {
        super(message);
    }

    public TransferFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
