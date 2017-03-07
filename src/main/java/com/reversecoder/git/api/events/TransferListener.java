package com.reversecoder.git.api.events;

public interface TransferListener {
    /**
     * @param transferEvent
     */
    void transferInitiated(TransferEvent transferEvent);

    /**
     * @param transferEvent
     */
    void transferStarted(TransferEvent transferEvent);

    /**
     * @param transferEvent
     */
    void transferProgress(TransferEvent transferEvent, byte[] buffer, int length);

    /**
     * @param transferEvent
     */
    void transferCompleted(TransferEvent transferEvent);

    /**
     * @param transferEvent
     */
    void transferError(TransferEvent transferEvent);

    /**
     * @param message
     */
    void debug(String message);

}
