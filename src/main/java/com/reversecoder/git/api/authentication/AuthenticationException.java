package com.reversecoder.git.api.authentication;

import com.reversecoder.git.api.GitApiException;

public class AuthenticationException extends GitApiException {

    public AuthenticationException(final String message) {
        super(message);
    }

    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
