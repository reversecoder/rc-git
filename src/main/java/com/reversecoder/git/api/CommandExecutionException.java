package com.reversecoder.git.api;

public class CommandExecutionException extends GitApiException {

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public CommandExecutionException(String message) {
        super(message);
    }

    /**
     * @see com.reversecoder.git.api.GitApiException
     */
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
