package com.reversecoder.git.api.observers;

import com.reversecoder.git.api.events.TransferEvent;
import com.reversecoder.git.api.events.TransferListener;

public abstract class AbstractTransferListener implements TransferListener {

    public void transferInitiated(TransferEvent transferEvent) {
    }

    /**
     * @see com.reversecoder.git.api.events.TransferListener#transferStarted(com.reversecoder.git.api.events.TransferEvent)
     */
    public void transferStarted(TransferEvent transferEvent) {
    }

    /**
     * @see com.reversecoder.git.api.events.TransferListener#transferProgress(com.reversecoder.git.api.events.TransferEvent,byte[],int)
     */
    public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length) {
    }

    public void transferCompleted(TransferEvent transferEvent) {
    }

    public void transferError(TransferEvent transferEvent) {
    }

    public void debug(String message) {
    }
}
