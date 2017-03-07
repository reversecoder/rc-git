package com.reversecoder.git.api;

public interface CommandExecutor extends GitApi {
    String ROLE = CommandExecutor.class.getName();

    void executeCommand(String command) throws CommandExecutionException;

    Streams executeCommand(String command, boolean ignoreFailures) throws CommandExecutionException;
}
