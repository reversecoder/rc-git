package com.reversecoder.git.api.observers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.reversecoder.git.api.events.TransferEvent;
import com.reversecoder.git.api.events.TransferListener;

public class ChecksumObserver implements TransferListener {
    private MessageDigest digester = null;

    private String actualChecksum;

    public ChecksumObserver() throws NoSuchAlgorithmException {
        this("MD5");
    }

    /**
     * @param algorithm
     *            One of the algorithms supported by JDK: MD5, MD2 or SHA-1
     */
    public ChecksumObserver(String algorithm) throws NoSuchAlgorithmException {
        digester = MessageDigest.getInstance(algorithm);
    }

    public void transferInitiated(TransferEvent transferEvent) {
        // This space left intentionally blank
    }

    /**
     * @see com.reversecoder.git.api.events.TransferListener#transferStarted(com.reversecoder.git.api.events.TransferEvent)
     */
    public void transferStarted(TransferEvent transferEvent) {
        actualChecksum = null;

        digester.reset();
    }

    /**
     * @see com.reversecoder.git.api.events.TransferListener#transferProgress(com.reversecoder.git.api.events.TransferEvent,
     *      byte[], int)
     */
    public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length) {
        digester.update(buffer, 0, length);
    }

    public void transferCompleted(TransferEvent transferEvent) {
        actualChecksum = encode(digester.digest());
    }

    public void transferError(TransferEvent transferEvent) {
        digester.reset();

        actualChecksum = null;
    }

    public void debug(String message) {
        // left intentionally blank
    }

    /**
     * Returns md5 checksum which was computed during transfer
     *
     * @return
     */
    public String getActualChecksum() {
        return actualChecksum;
    }

    /**
     * Encodes a 128 bit or 160-bit byte array into a String.
     *
     * @param binaryData
     *            Array containing the digest
     * @return Encoded hex string, or null if encoding failed
     */
    @SuppressWarnings("checkstyle:magicnumber")
    protected String encode(byte[] binaryData) {

        if (binaryData.length != 16 && binaryData.length != 20) {
            int bitLength = binaryData.length * 8;
            throw new IllegalArgumentException(
                    "Unrecognised length for binary data: " + bitLength + " bits");
        }

        StringBuilder retValue = new StringBuilder();

        for (byte b : binaryData) {
            String t = Integer.toHexString(b & 0xff);

            if (t.length() == 1) {
                retValue.append('0').append(t);
            } else {
                retValue.append(t);
            }
        }

        return retValue.toString().trim();
    }

}
