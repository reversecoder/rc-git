package com.reversecoder.git.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.reversecoder.git.api.authorization.AuthorizationException;
import com.reversecoder.git.api.events.TransferEvent;
import com.reversecoder.git.api.resource.Resource;

public abstract class StreamGitApi extends AbstractGitApi implements StreamingGitApi {
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public abstract void fillInputData(InputData inputData)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;

    public abstract void fillOutputData(OutputData outputData) throws TransferFailedException;

    public abstract void closeConnection() throws ConnectionException;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void get(String resourceName, File destination)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        getIfNewer(resourceName, destination, 0);
    }

    protected void checkInputStream(InputStream is, Resource resource)
            throws TransferFailedException {
        if (is == null) {
            TransferFailedException e = new TransferFailedException(getRepository().getUrl()
                    + " - Could not open input stream for resource: '" + resource + "'");
            fireTransferError(resource, e, TransferEvent.REQUEST_GET);
            throw e;
        }
    }

    public boolean getIfNewer(String resourceName, File destination, long timestamp)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        boolean retValue = false;

        Resource resource = new Resource(resourceName);

        fireGetInitiated(resource, destination);

        resource.setLastModified(timestamp);

        InputStream is = getInputStream(resource);

        // always get if timestamp is 0 (ie, target doesn't exist), otherwise
        // only if older than the remote file
        if (timestamp == 0 || timestamp < resource.getLastModified()) {
            retValue = true;

            checkInputStream(is, resource);

            getTransfer(resource, destination, is);
        } else {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException e) {
                throw new TransferFailedException("Failure transferring " + resourceName, e);
            }
        }

        return retValue;
    }

    protected InputStream getInputStream(Resource resource)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        InputData inputData = new InputData();

        inputData.setResource(resource);

        try {
            fillInputData(inputData);
        } catch (TransferFailedException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_GET);
            cleanupGetTransfer(resource);
            throw e;
        } catch (ResourceDoesNotExistException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_GET);
            cleanupGetTransfer(resource);
            throw e;
        } catch (AuthorizationException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_GET);
            cleanupGetTransfer(resource);
            throw e;
        } finally {
            if (inputData.getInputStream() == null) {
                cleanupGetTransfer(resource);
            }
        }

        return inputData.getInputStream();
    }

    // source doesn't exist exception
    public void put(File source, String resourceName)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        Resource resource = new Resource(resourceName);

        firePutInitiated(resource, source);

        resource.setContentLength(source.length());

        resource.setLastModified(source.lastModified());

        OutputStream os = getOutputStream(resource);

        checkOutputStream(resource, os);

        putTransfer(resource, source, os, true);
    }

    protected void checkOutputStream(Resource resource, OutputStream os)
            throws TransferFailedException {
        if (os == null) {
            TransferFailedException e = new TransferFailedException(getRepository().getUrl()
                    + " - Could not open output stream for resource: '" + resource + "'");
            fireTransferError(resource, e, TransferEvent.REQUEST_PUT);
            throw e;
        }
    }

    protected OutputStream getOutputStream(Resource resource) throws TransferFailedException {
        OutputData outputData = new OutputData();

        outputData.setResource(resource);

        try {
            fillOutputData(outputData);
        } catch (TransferFailedException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_PUT);

            throw e;
        } finally {
            if (outputData.getOutputStream() == null) {
                cleanupPutTransfer(resource);
            }
        }

        return outputData.getOutputStream();
    }

    public boolean getIfNewerToStream(String resourceName, OutputStream stream, long timestamp)
            throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {
        boolean retValue = false;

        Resource resource = new Resource(resourceName);

        fireGetInitiated(resource, null);

        InputStream is = getInputStream(resource);

        // always get if timestamp is 0 (ie, target doesn't exist), otherwise
        // only if older than the remote file
        if (timestamp == 0 || timestamp < resource.getLastModified()) {
            retValue = true;

            checkInputStream(is, resource);

            fireGetStarted(resource, null);

            getTransfer(resource, stream, is, true, Integer.MAX_VALUE);

            fireGetCompleted(resource, null);
        } else {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException e) {
                throw new TransferFailedException("Failure transferring " + resourceName, e);
            }
        }

        return retValue;
    }

    public void getToStream(String resourceName, OutputStream stream)
            throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {
        getIfNewerToStream(resourceName, stream, 0);
    }

    public void putFromStream(InputStream stream, String destination)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        Resource resource = new Resource(destination);

        firePutInitiated(resource, null);

        putFromStream(stream, resource);
    }

    public void putFromStream(InputStream stream, String destination, long contentLength,
            long lastModified) throws TransferFailedException, ResourceDoesNotExistException,
                    AuthorizationException {
        Resource resource = new Resource(destination);

        firePutInitiated(resource, null);

        resource.setContentLength(contentLength);

        resource.setLastModified(lastModified);

        putFromStream(stream, resource);
    }

    protected void putFromStream(InputStream stream, Resource resource)
            throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
        OutputStream os = getOutputStream(resource);

        checkOutputStream(resource, os);

        firePutStarted(resource, null);

        putTransfer(resource, stream, os, true);

        firePutCompleted(resource, null);
    }
}
