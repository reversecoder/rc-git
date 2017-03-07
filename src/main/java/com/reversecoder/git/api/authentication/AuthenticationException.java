package com.reversecoder.git.api.authentication;

import com.reversecoder.git.api.GitApiException;

public class AuthenticationException extends GitApiException {

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public AuthenticationException(final String message) {
        super(message);
    }

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
