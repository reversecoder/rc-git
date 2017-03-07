package com.reversecoder.git.api;

public class ConnectionException extends GitApiException {

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public ConnectionException(final String message) {
        super(message);
    }

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
