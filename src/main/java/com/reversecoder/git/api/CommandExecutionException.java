package com.reversecoder.git.api;

public class CommandExecutionException extends GitApiException {

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
