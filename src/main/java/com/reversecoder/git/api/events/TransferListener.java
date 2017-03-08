package com.reversecoder.git.api.events;

public interface TransferListener {

    void transferInitiated(TransferEvent transferEvent);

    void transferStarted(TransferEvent transferEvent);

    void transferProgress(TransferEvent transferEvent, byte[] buffer, int length);

    void transferCompleted(TransferEvent transferEvent);

    void transferError(TransferEvent transferEvent);

    void debug(String message);
}
