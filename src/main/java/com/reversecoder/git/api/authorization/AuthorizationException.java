package com.reversecoder.git.api.authorization;

import com.reversecoder.git.api.GitApiException;

public class AuthorizationException extends GitApiException {

    public AuthorizationException(final String message) {
        super(message);
    }

    public AuthorizationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
