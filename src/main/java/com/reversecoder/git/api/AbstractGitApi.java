package com.reversecoder.git.api;

import com.reversecoder.git.api.authentication.AuthenticationException;
import com.reversecoder.git.api.authentication.AuthenticationInfo;
import com.reversecoder.git.api.authorization.AuthorizationException;
import com.reversecoder.git.api.events.SessionEvent;
import com.reversecoder.git.api.events.SessionEventSupport;
import com.reversecoder.git.api.events.SessionListener;
import com.reversecoder.git.api.events.TransferEvent;
import com.reversecoder.git.api.events.TransferEventSupport;
import com.reversecoder.git.api.events.TransferListener;
import com.reversecoder.git.api.proxy.ProxyInfo;
import com.reversecoder.git.api.proxy.ProxyInfoProvider;
import com.reversecoder.git.api.proxy.ProxyUtils;
import com.reversecoder.git.api.repository.Repository;
import com.reversecoder.git.api.repository.RepositoryPermissions;
import com.reversecoder.git.api.resource.Resource;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class AbstractGitApi implements GitApi {
    protected static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    protected Repository repository;

    protected SessionEventSupport sessionEventSupport = new SessionEventSupport();

    protected TransferEventSupport transferEventSupport = new TransferEventSupport();

    protected AuthenticationInfo authenticationInfo;

    protected boolean interactive = true;

    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    /**
     * read timeout value
     *
     * @since 2.2
     */
    private int readTimeout = Integer.parseInt(
            System.getProperty("git.read.time.out", Integer.toString(GitApi.DEFAULT_READ_TIMEOUT)));

    private ProxyInfoProvider proxyInfoProvider;

    /**
     * @deprecated
     */
    protected ProxyInfo proxyInfo;

    private RepositoryPermissions permissionsOverride;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Repository getRepository() {
        return repository;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfoProvider != null ? proxyInfoProvider.getProxyInfo(null) : null;
    }

    public AuthenticationInfo getAuthenticationInfo() {
        return authenticationInfo;
    }

    // ----------------------------------------------------------------------
    // Connection
    // ----------------------------------------------------------------------

    public void openConnection() throws ConnectionException, AuthenticationException {
        try {
            openConnectionInternal();
        } catch (ConnectionException e) {
            fireSessionConnectionRefused();

            throw e;
        } catch (AuthenticationException e) {
            fireSessionConnectionRefused();

            throw e;
        }
    }

    public void connect(Repository repository) throws ConnectionException, AuthenticationException {
        connect(repository, null, (ProxyInfoProvider) null);
    }

    public void connect(Repository repository, ProxyInfo proxyInfo)
            throws ConnectionException, AuthenticationException {
        connect(repository, null, proxyInfo);
    }

    public void connect(Repository repository, ProxyInfoProvider proxyInfoProvider)
            throws ConnectionException, AuthenticationException {
        connect(repository, null, proxyInfoProvider);
    }

    public void connect(Repository repository, AuthenticationInfo authenticationInfo)
            throws ConnectionException, AuthenticationException {
        connect(repository, authenticationInfo, (ProxyInfoProvider) null);
    }

    public void connect(Repository repository, AuthenticationInfo authenticationInfo,
            ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException {
        final ProxyInfo proxy = proxyInfo;
        connect(repository, authenticationInfo, new ProxyInfoProvider() {
            public ProxyInfo getProxyInfo(String protocol) {
                if (protocol == null || proxy == null
                        || protocol.equalsIgnoreCase(proxy.getType())) {
                    return proxy;
                } else {
                    return null;
                }
            }
        });
    }

    public void connect(Repository repository, AuthenticationInfo authenticationInfo,
            ProxyInfoProvider proxyInfoProvider)
                    throws ConnectionException, AuthenticationException {
        if (repository == null) {
            throw new NullPointerException("repository cannot be null");
        }

        if (permissionsOverride != null) {
            repository.setPermissions(permissionsOverride);
        }

        this.repository = repository;

        if (authenticationInfo == null) {
            authenticationInfo = new AuthenticationInfo();
        }

        if (authenticationInfo.getUserName() == null) {
            // Get user/pass that were encoded in the URL.
            if (repository.getUsername() != null) {
                authenticationInfo.setUserName(repository.getUsername());
                if (repository.getPassword() != null && authenticationInfo.getPassword() == null) {
                    authenticationInfo.setPassword(repository.getPassword());
                }
            }
        }

        this.authenticationInfo = authenticationInfo;

        this.proxyInfoProvider = proxyInfoProvider;

        fireSessionOpening();

        openConnection();

        fireSessionOpened();
    }

    protected abstract void openConnectionInternal()
            throws ConnectionException, AuthenticationException;

    public void disconnect() throws ConnectionException {
        fireSessionDisconnecting();

        try {
            closeConnection();
        } catch (ConnectionException e) {
            fireSessionError(e);
            throw e;
        }

        fireSessionDisconnected();
    }

    protected abstract void closeConnection() throws ConnectionException;

    protected void createParentDirectories(File destination) throws TransferFailedException {
        File destinationDirectory = destination.getParentFile();
        try {
            destinationDirectory = destinationDirectory.getCanonicalFile();
        } catch (IOException e) {
            // not essential to have a canonical file
        }
        if (destinationDirectory != null && !destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
            if (!destinationDirectory.exists()) {
                throw new TransferFailedException(
                        "Specified destination directory cannot be created: "
                                + destinationDirectory);
            }
        }
    }

    public void setTimeout(int timeoutValue) {
        connectionTimeout = timeoutValue;
    }

    public int getTimeout() {
        return connectionTimeout;
    }

    // ----------------------------------------------------------------------
    // Stream i/o
    // ----------------------------------------------------------------------

    protected void getTransfer(Resource resource, File destination, InputStream input)
            throws TransferFailedException {
        getTransfer(resource, destination, input, true, Long.MAX_VALUE);
    }

    protected void getTransfer(Resource resource, OutputStream output, InputStream input)
            throws TransferFailedException {
        getTransfer(resource, output, input, true, Long.MAX_VALUE);
    }

    @Deprecated
    protected void getTransfer(Resource resource, File destination, InputStream input,
            boolean closeInput, int maxSize) throws TransferFailedException {
        getTransfer(resource, destination, input, closeInput, (long) maxSize);
    }

    protected void getTransfer(Resource resource, File destination, InputStream input,
            boolean closeInput, long maxSize) throws TransferFailedException {
        // ensure that the destination is created only when we are ready to
        // transfer
        fireTransferDebug("attempting to create parent directories for destination: "
                + destination.getName());
        createParentDirectories(destination);

        fireGetStarted(resource, destination);

        OutputStream output = null;
        try {
            output = new LazyFileOutputStream(destination);
            getTransfer(resource, output, input, closeInput, maxSize);
            output.close();
            output = null;
        } catch (final IOException e) {
            if (destination.exists()) {
                boolean deleted = destination.delete();

                if (!deleted) {
                    destination.deleteOnExit();
                }
            }

            fireTransferError(resource, e, TransferEvent.REQUEST_GET);

            String msg = "GET request of: " + resource.getName() + " from " + repository.getName()
                    + " failed";

            throw new TransferFailedException(msg, e);
        } catch (TransferFailedException e) {
            if (destination.exists()) {
                boolean deleted = destination.delete();

                if (!deleted) {
                    destination.deleteOnExit();
                }
            }
            throw e;
        } finally {
            IOUtil.close(output);
        }

        fireGetCompleted(resource, destination);
    }

    @Deprecated
    protected void getTransfer(Resource resource, OutputStream output, InputStream input,
            boolean closeInput, int maxSize) throws TransferFailedException {
        getTransfer(resource, output, input, closeInput, (long) maxSize);
    }

    protected void getTransfer(Resource resource, OutputStream output, InputStream input,
            boolean closeInput, long maxSize) throws TransferFailedException {
        try {
            transfer(resource, input, output, TransferEvent.REQUEST_GET, maxSize);

            finishGetTransfer(resource, input, output);

            if (closeInput) {
                input.close();
                input = null;
            }

        } catch (IOException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_GET);

            String msg = "GET request of: " + resource.getName() + " from " + repository.getName()
                    + " failed";

            throw new TransferFailedException(msg, e);
        } finally {
            if (closeInput) {
                IOUtil.close(input);
            }

            cleanupGetTransfer(resource);
        }
    }

    protected void finishGetTransfer(Resource resource, InputStream input, OutputStream output)
            throws TransferFailedException {
    }

    protected void cleanupGetTransfer(Resource resource) {
    }

    protected void putTransfer(Resource resource, File source, OutputStream output,
            boolean closeOutput) throws TransferFailedException, AuthorizationException,
                    ResourceDoesNotExistException {
        firePutStarted(resource, source);

        transfer(resource, source, output, closeOutput);

        firePutCompleted(resource, source);
    }

    /**
     * Write from {@link File} to {@link OutputStream}
     *
     * @param resource
     *            resource to transfer
     * @param source
     *            file to read from
     * @param output
     *            output stream
     * @param closeOutput
     *            whether the output stream should be closed or not
     * @throws TransferFailedException Throws transfer failed exception.
     * @throws ResourceDoesNotExistException Throws resource does not exist exception.
     * @throws AuthorizationException Throws authorization exception.
     * @since 1.0-beta-1
     */
    protected void transfer(Resource resource, File source, OutputStream output,
            boolean closeOutput) throws TransferFailedException, AuthorizationException,
                    ResourceDoesNotExistException {
        InputStream input = null;

        try {
            input = new FileInputStream(source);

            putTransfer(resource, input, output, closeOutput);

            input.close();
            input = null;
        } catch (FileNotFoundException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_PUT);

            throw new TransferFailedException("Specified source file does not exist: " + source, e);
        } catch (final IOException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_PUT);

            throw new TransferFailedException("Failure transferring " + source, e);
        } finally {
            IOUtil.close(input);
        }
    }

    protected void putTransfer(Resource resource, InputStream input, OutputStream output,
            boolean closeOutput) throws TransferFailedException, AuthorizationException,
                    ResourceDoesNotExistException {
        try {
            transfer(resource, input, output, TransferEvent.REQUEST_PUT,
                    resource.getContentLength() == GitApiConstants.UNKNOWN_LENGTH ? Long.MAX_VALUE
                            : resource.getContentLength());

            finishPutTransfer(resource, input, output);

            if (closeOutput) {
                output.close();
                output = null;
            }
        } catch (IOException e) {
            fireTransferError(resource, e, TransferEvent.REQUEST_PUT);

            String msg = "PUT request to: " + resource.getName() + " in " + repository.getName()
                    + " failed";

            throw new TransferFailedException(msg, e);
        } finally {
            if (closeOutput) {
                IOUtil.close(output);
            }

            cleanupPutTransfer(resource);
        }
    }

    protected void cleanupPutTransfer(Resource resource) {
    }

    protected void finishPutTransfer(Resource resource, InputStream input, OutputStream output)
            throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
    }

    /**
     * Write from {@link InputStream} to {@link OutputStream}. Equivalent to
     * {@link #transfer(Resource, InputStream, OutputStream, int, int)} with a
     * maxSize equals to {@link Integer#MAX_VALUE}
     *
     * @param resource
     *            resource to transfer
     * @param input
     *            input stream
     * @param output
     *            output stream
     * @param requestType
     *            one of {@link TransferEvent#REQUEST_GET} or
     *            {@link TransferEvent#REQUEST_PUT}
     * @throws IOException Throws IO exception.
     */
    protected void transfer(Resource resource, InputStream input, OutputStream output,
            int requestType) throws IOException {
        transfer(resource, input, output, requestType, Long.MAX_VALUE);
    }

    /**
     * Write from {@link InputStream} to {@link OutputStream}. Equivalent to
     * {@link #transfer(Resource, InputStream, OutputStream, int, int)} with a
     * maxSize equals to {@link Integer#MAX_VALUE}
     *
     * @param resource
     *            resource to transfer
     * @param input
     *            input stream
     * @param output
     *            output stream
     * @param requestType
     *            one of {@link TransferEvent#REQUEST_GET} or
     *            {@link TransferEvent#REQUEST_PUT}
     * @param maxSize
     *            size of the buffer
     * @throws IOException Throws IO exception.
     * @deprecated Please use the transfer using long as type of maxSize
     */
    @Deprecated
    protected void transfer(Resource resource, InputStream input, OutputStream output,
            int requestType, int maxSize) throws IOException {
        transfer(resource, input, output, requestType, (long) maxSize);
    }

    /**
     * Write from {@link InputStream} to {@link OutputStream}. Equivalent to
     * {@link #transfer(Resource, InputStream, OutputStream, int, long)} with a
     * maxSize equals to {@link Integer#MAX_VALUE}
     *
     * @param resource
     *            resource to transfer
     * @param input
     *            input stream
     * @param output
     *            output stream
     * @param requestType
     *            one of {@link TransferEvent#REQUEST_GET} or
     *            {@link TransferEvent#REQUEST_PUT}
     * @param maxSize
     *            size of the buffer
     * @throws IOException Throws IO exception.
     */
    protected void transfer(Resource resource, InputStream input, OutputStream output,
            int requestType, long maxSize) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_PROGRESS, requestType);
        transferEvent.setTimestamp(System.currentTimeMillis());

        long remaining = maxSize;
        while (remaining > 0) {
            // let's safely cast to int because the min value will be lower than
            // the buffer size.
            int n = input.read(buffer, 0, (int) Math.min(buffer.length, remaining));

            if (n == -1) {
                break;
            }

            fireTransferProgress(transferEvent, buffer, n);

            output.write(buffer, 0, n);

            remaining -= n;
        }
        output.flush();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void fireTransferProgress(TransferEvent transferEvent, byte[] buffer, int n) {
        transferEventSupport.fireTransferProgress(transferEvent, buffer, n);
    }

    protected void fireGetCompleted(Resource resource, File localFile) {
        long timestamp = System.currentTimeMillis();

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_COMPLETED, TransferEvent.REQUEST_GET);

        transferEvent.setTimestamp(timestamp);

        transferEvent.setLocalFile(localFile);

        transferEventSupport.fireTransferCompleted(transferEvent);
    }

    protected void fireGetStarted(Resource resource, File localFile) {
        long timestamp = System.currentTimeMillis();

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_STARTED, TransferEvent.REQUEST_GET);

        transferEvent.setTimestamp(timestamp);

        transferEvent.setLocalFile(localFile);

        transferEventSupport.fireTransferStarted(transferEvent);
    }

    protected void fireGetInitiated(Resource resource, File localFile) {
        long timestamp = System.currentTimeMillis();

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_INITIATED, TransferEvent.REQUEST_GET);

        transferEvent.setTimestamp(timestamp);

        transferEvent.setLocalFile(localFile);

        transferEventSupport.fireTransferInitiated(transferEvent);
    }

    protected void firePutInitiated(Resource resource, File localFile) {
        long timestamp = System.currentTimeMillis();

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_INITIATED, TransferEvent.REQUEST_PUT);

        transferEvent.setTimestamp(timestamp);

        transferEvent.setLocalFile(localFile);

        transferEventSupport.fireTransferInitiated(transferEvent);
    }

    protected void firePutCompleted(Resource resource, File localFile) {
        long timestamp = System.currentTimeMillis();

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_COMPLETED, TransferEvent.REQUEST_PUT);

        transferEvent.setTimestamp(timestamp);

        transferEvent.setLocalFile(localFile);

        transferEventSupport.fireTransferCompleted(transferEvent);
    }

    protected void firePutStarted(Resource resource, File localFile) {
        long timestamp = System.currentTimeMillis();

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_STARTED, TransferEvent.REQUEST_PUT);

        transferEvent.setTimestamp(timestamp);

        transferEvent.setLocalFile(localFile);

        transferEventSupport.fireTransferStarted(transferEvent);
    }

    protected void fireSessionDisconnected() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_DISCONNECTED);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionDisconnected(sessionEvent);
    }

    protected void fireSessionDisconnecting() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_DISCONNECTING);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionDisconnecting(sessionEvent);
    }

    protected void fireSessionLoggedIn() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_LOGGED_IN);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionLoggedIn(sessionEvent);
    }

    protected void fireSessionLoggedOff() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_LOGGED_OFF);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionLoggedOff(sessionEvent);
    }

    protected void fireSessionOpened() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_OPENED);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionOpened(sessionEvent);
    }

    protected void fireSessionOpening() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_OPENING);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionOpening(sessionEvent);
    }

    protected void fireSessionConnectionRefused() {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, SessionEvent.SESSION_CONNECTION_REFUSED);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionConnectionRefused(sessionEvent);
    }

    protected void fireSessionError(Exception exception) {
        long timestamp = System.currentTimeMillis();

        SessionEvent sessionEvent = new SessionEvent(this, exception);

        sessionEvent.setTimestamp(timestamp);

        sessionEventSupport.fireSessionError(sessionEvent);

    }

    protected void fireTransferDebug(String message) {
        transferEventSupport.fireDebug(message);
    }

    protected void fireSessionDebug(String message) {
        sessionEventSupport.fireDebug(message);
    }

    public boolean hasTransferListener(TransferListener listener) {
        return transferEventSupport.hasTransferListener(listener);
    }

    public void addTransferListener(TransferListener listener) {
        transferEventSupport.addTransferListener(listener);
    }

    public void removeTransferListener(TransferListener listener) {
        transferEventSupport.removeTransferListener(listener);
    }

    public void addSessionListener(SessionListener listener) {
        sessionEventSupport.addSessionListener(listener);
    }

    public boolean hasSessionListener(SessionListener listener) {
        return sessionEventSupport.hasSessionListener(listener);
    }

    public void removeSessionListener(SessionListener listener) {
        sessionEventSupport.removeSessionListener(listener);
    }

    protected void fireTransferError(Resource resource, Exception e, int requestType) {
        TransferEvent transferEvent = new TransferEvent(this, resource, e, requestType);
        transferEventSupport.fireTransferError(transferEvent);
    }

    public SessionEventSupport getSessionEventSupport() {
        return sessionEventSupport;
    }

    public void setSessionEventSupport(SessionEventSupport sessionEventSupport) {
        this.sessionEventSupport = sessionEventSupport;
    }

    public TransferEventSupport getTransferEventSupport() {
        return transferEventSupport;
    }

    public void setTransferEventSupport(TransferEventSupport transferEventSupport) {
        this.transferEventSupport = transferEventSupport;
    }

    /**
     * This method is used if you are not streaming the transfer, to make sure
     * any listeners dependent on state (eg checksum observers) succeed.
     *
     * @param resource the resource.
     * @param source the source.
     * @param requestType the request type.
     * @throws TransferFailedException throws transfer failed exception.
     */
    protected void postProcessListeners(Resource resource, File source, int requestType)
            throws TransferFailedException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        TransferEvent transferEvent = new TransferEvent(this, resource,
                TransferEvent.TRANSFER_PROGRESS, requestType);
        transferEvent.setTimestamp(System.currentTimeMillis());
        transferEvent.setLocalFile(source);

        InputStream input = null;
        try {
            input = new FileInputStream(source);

            while (true) {
                int n = input.read(buffer);

                if (n == -1) {
                    break;
                }

                fireTransferProgress(transferEvent, buffer, n);
            }

            input.close();
            input = null;
        } catch (IOException e) {
            fireTransferError(resource, e, requestType);

            throw new TransferFailedException("Failed to post-process the source file", e);
        } finally {
            IOUtil.close(input);
        }
    }

    public void putDirectory(File sourceDirectory, String destinationDirectory)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        throw new UnsupportedOperationException(
                "The rc-git you are using has not implemented putDirectory()");
    }

    public boolean supportsDirectoryCopy() {
        return false;
    }

    protected static String getPath(String basedir, String dir) {
        String path;
        path = basedir;
        if (!basedir.endsWith("/") && !dir.startsWith("/")) {
            path += "/";
        }
        path += dir;
        return path;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public List<String> getFileList(String destinationDirectory)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        throw new UnsupportedOperationException(
                "The rc-git you are using has not implemented getFileList()");
    }

    public boolean resourceExists(String resourceName)
            throws TransferFailedException, AuthorizationException {
        throw new UnsupportedOperationException(
                "The rc-git you are using has not implemented resourceExists()");
    }

    protected ProxyInfo getProxyInfo(String protocol, String host) {
        if (proxyInfoProvider != null) {
            ProxyInfo proxyInfo = proxyInfoProvider.getProxyInfo(protocol);
            if (!ProxyUtils.validateNonProxyHosts(proxyInfo, host)) {
                return proxyInfo;
            }
        }
        return null;
    }

    public RepositoryPermissions getPermissionsOverride() {
        return permissionsOverride;
    }

    public void setPermissionsOverride(RepositoryPermissions permissionsOverride) {
        this.permissionsOverride = permissionsOverride;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }
}