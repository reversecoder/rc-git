package com.reversecoder.git.api;

public class TransferFailedException extends GitApiException {

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public TransferFailedException(final String message) {
        super(message);
    }

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public TransferFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
