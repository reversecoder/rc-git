package com.reversecoder.git.api;

public class UnsupportedProtocolException extends GitApiException {

    /**
     * @see GitApiException
     */
    public UnsupportedProtocolException(final String message) {
        super(message);
    }

    /**
     * @see GitApiException
     */
    public UnsupportedProtocolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
