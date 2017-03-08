package com.reversecoder.git.api.events;

import com.reversecoder.git.api.GitApi;
import com.reversecoder.git.api.resource.Resource;

import java.io.File;

public class TransferEvent extends GitApiEvent {

    /**
     * A transfer was attempted, but has not yet commenced.
     */
    public static final int TRANSFER_INITIATED = 0;

    /**
     * A transfer was started.
     */
    public static final int TRANSFER_STARTED = 1;

    /**
     * A transfer is completed.
     */
    public static final int TRANSFER_COMPLETED = 2;

    /**
     * A transfer is in progress.
     */
    public static final int TRANSFER_PROGRESS = 3;

    /**
     * An error occurred during transfer
     */
    public static final int TRANSFER_ERROR = 4;

    /**
     * Indicates GET transfer (from the repository)
     */
    public static final int REQUEST_GET = 5;

    /**
     * Indicates PUT transfer (to the repository)
     */
    public static final int REQUEST_PUT = 6;

    private Resource resource;

    private int eventType;

    private int requestType;

    private Exception exception;

    private File localFile;

    public TransferEvent(final GitApi gitApi, final Resource resource, final int eventType,
            final int requestType) {
        super(gitApi);

        this.resource = resource;

        setEventType(eventType);

        setRequestType(requestType);

    }

    public TransferEvent(final GitApi gitApi, final Resource resource, final Exception exception,
            final int requestType) {
        this(gitApi, resource, TRANSFER_ERROR, requestType);

        this.exception = exception;
    }

    /**
     * @return Returns the resource.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * @return Returns the exception.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the request type.
     *
     * @return Returns the request type. The Request type is one of
     *         <code>TransferEvent.REQUEST_GET</code> or
     *         <code>TransferEvent.REQUEST_PUT</code>
     */
    public int getRequestType() {
        return requestType;
    }

    /**
     * Sets the request type
     *
     * @param requestType
     *            The requestType to set. The Request type value should be
     *            either <code>TransferEvent.REQUEST_GET</code> or
     *            <code>TransferEvent.REQUEST_PUT</code>.
     * @throws IllegalArgumentException
     *             when
     */
    public void setRequestType(final int requestType) {
        switch (requestType) {

        case REQUEST_PUT:
        case REQUEST_GET:
            break;

        default:
            throw new IllegalArgumentException("Illegal request type: " + requestType);
        }

        this.requestType = requestType;
    }

    /**
     * @return Returns the eventType.
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * @param eventType
     *            The eventType to set.
     */
    public void setEventType(final int eventType) {
        switch (eventType) {

        case TRANSFER_INITIATED:
        case TRANSFER_STARTED:
        case TRANSFER_COMPLETED:
        case TRANSFER_PROGRESS:
        case TRANSFER_ERROR:
            break;
        default:
            throw new IllegalArgumentException("Illegal event type: " + eventType);
        }

        this.eventType = eventType;
    }

    /**
     * @param resource
     *            The resource to set.
     */
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    /**
     * @return Returns the local file.
     */
    public File getLocalFile() {
        return localFile;
    }

    /**
     * @param localFile
     *            The local file to set.
     */
    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TransferEvent[");

        switch (this.getRequestType()) {
        case REQUEST_GET:
            sb.append("GET");
            break;
        case REQUEST_PUT:
            sb.append("PUT");
            break;
        default:
            sb.append(this.getRequestType());
            break;
        }

        sb.append("|");
        switch (this.getEventType()) {
        case TRANSFER_COMPLETED:
            sb.append("COMPLETED");
            break;
        case TRANSFER_ERROR:
            sb.append("ERROR");
            break;
        case TRANSFER_INITIATED:
            sb.append("INITIATED");
            break;
        case TRANSFER_PROGRESS:
            sb.append("PROGRESS");
            break;
        case TRANSFER_STARTED:
            sb.append("STARTED");
            break;
        default:
            sb.append(this.getEventType());
            break;
        }

        sb.append("|");

        sb.append(this.getGitApi().getRepository()).append("|");
        sb.append(this.getLocalFile()).append("|");
        sb.append(this.getResource().inspect());
        sb.append("]");

        return sb.toString();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + eventType;
        result = prime * result + ((exception == null) ? 0 : exception.hashCode());
        result = prime * result + ((localFile == null) ? 0 : localFile.hashCode());
        result = prime * result + requestType;
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final TransferEvent other = (TransferEvent) obj;
        if (eventType != other.eventType) {
            return false;
        }
        if (exception == null) {
            if (other.exception != null) {
                return false;
            }
        } else if (!exception.getClass().equals(other.exception.getClass())) {
            return false;
        }
        if (requestType != other.requestType) {
            return false;
        }
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        } else if (!resource.equals(other.resource)) {
            return false;
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }

}
