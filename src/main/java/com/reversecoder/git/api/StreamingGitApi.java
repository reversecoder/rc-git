package com.reversecoder.git.api;

import java.io.InputStream;
import java.io.OutputStream;

import com.reversecoder.git.api.authorization.AuthorizationException;

public interface StreamingGitApi extends GitApi {
    /**
     * Downloads specified resource from the repository to given output stream.
     * 
     * @param resourceName
     * @param stream
     * @throws TransferFailedException
     * @throws ResourceDoesNotExistException
     * @throws AuthorizationException
     * @throws AuthorizationException
     */
    void getToStream(String resourceName, OutputStream stream)
            throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException;

    /**
     * Downloads specified resource from the repository if it was modified since
     * specified date. The date is measured in milliseconds, between the current
     * time and midnight, January 1, 1970 UTC and aligned to GMT timezone.
     * 
     * @param resourceName
     * @param stream
     * @param timestamp
     * @return <code>true</code> if newer resource has been downloaded,
     *         <code>false</code> if resource in the repository is older or has
     *         the same age.
     * @throws TransferFailedException
     * @throws ResourceDoesNotExistException
     * @throws AuthorizationException
     * @throws AuthorizationException
     */
    boolean getIfNewerToStream(String resourceName, OutputStream stream, long timestamp)
            throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException;

    /**
     * @deprecated due to unknown contentLength various http(s) implementation
     *             will use a chuncked transfer encoding mode you must take care
     *             you http target server supports that (ngnix don't !). <b>So
     *             in case of http(s) transport layer avoid using this. Will be
     *             remove in 3.0</b> Copy from a local input stream to remote.
     * 
     * @param stream
     *            the local stream
     * @param destination
     *            the remote destination
     * @throws TransferFailedException
     * @throws ResourceDoesNotExistException
     * @throws AuthorizationException
     */
    void putFromStream(InputStream stream, String destination)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;

    /**
     * Copy from a local input stream to remote.
     * 
     * @param stream
     *            the local stream
     * @param destination
     *            the remote destination
     * @throws TransferFailedException
     * @throws ResourceDoesNotExistException
     * @throws AuthorizationException
     */
    void putFromStream(InputStream stream, String destination, long contentLength,
            long lastModified) throws TransferFailedException, ResourceDoesNotExistException,
                    AuthorizationException;
}
