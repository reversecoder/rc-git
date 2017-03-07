package com.reversecoder.git.api.events;

import com.reversecoder.git.api.GitApi;

public class SessionEvent extends GitApiEvent {

    /**
     * A SESSION was closed.
     */
    public static final int SESSION_CLOSED = 1;

    /**
     * A SESSION is about to be disconnected.
     */
    public static final int SESSION_DISCONNECTING = 2;

    /**
     * A SESSION was disconnected (not currently used).
     */
    public static final int SESSION_DISCONNECTED = 3;

    /**
     * A SESSION was refused.
     */
    public static final int SESSION_CONNECTION_REFUSED = 4;

    /**
     * A SESSION is about to be opened.
     */
    public static final int SESSION_OPENING = 5;

    /**
     * A SESSION was opened.
     */
    public static final int SESSION_OPENED = 6;

    /**
     * A SESSION was opened.
     */
    public static final int SESSION_LOGGED_IN = 7;

    /**
     * A SESSION was opened.
     */
    public static final int SESSION_LOGGED_OFF = 8;

    /**
     * A SESSION was opened.
     */
    public static final int SESSION_ERROR_OCCURRED = 9;

    /**
     * The type of the event. One of the SESSSION_XXX constants
     */
    private int eventType;

    private Exception exception;

    /**
     * Creates new instance of SessionEvent
     *
     * @param gitApi
     *            <code>GitApi<code> object which created this event
     * @param eventType
     *            the type of the event
     */
    public SessionEvent(final GitApi gitApi, final int eventType) {
        super(gitApi);
        this.eventType = eventType;

    }

    /**
     * Creates new instance of SessionEvent. Sets event type to
     * <code>SESSION_ERROR_OCCURRED</code>
     *
     * @param gitApi
     *            <code>GitApi<code> object which created this event
     * @param exception
     *            the exception
     */
    public SessionEvent(final GitApi gitApi, final Exception exception) {
        super(gitApi);
        this.exception = exception;
        this.eventType = SESSION_ERROR_OCCURRED;

    }

    /**
     * @return Returns the type.
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * @return Returns the exception.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param eventType
     *            The eventType to set.
     */
    public void setEventType(final int eventType) {
        switch (eventType) {
        case SessionEvent.SESSION_CLOSED:
        case SessionEvent.SESSION_DISCONNECTED:
        case SessionEvent.SESSION_DISCONNECTING:
        case SessionEvent.SESSION_ERROR_OCCURRED:
        case SessionEvent.SESSION_LOGGED_IN:
        case SessionEvent.SESSION_LOGGED_OFF:
        case SessionEvent.SESSION_OPENED:
        case SessionEvent.SESSION_OPENING:
        case SessionEvent.SESSION_CONNECTION_REFUSED:
            break;
        default:
            throw new IllegalArgumentException("Illegal event type: " + eventType);
        }
        this.eventType = eventType;
    }

    /**
     * @param exception
     *            The exception to set.
     */
    public void setException(final Exception exception) {
        this.exception = exception;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("SessionEvent[");

        switch (this.eventType) {
        case SessionEvent.SESSION_CLOSED:
            sb.append("CONNECTION_CLOSED");
            break;
        case SessionEvent.SESSION_DISCONNECTED:
            sb.append("CONNECTION_DISCONNECTED");
            break;
        case SessionEvent.SESSION_DISCONNECTING:
            sb.append("CONNECTION_DISCONNECTING");
            break;
        case SessionEvent.SESSION_ERROR_OCCURRED:
            sb.append("CONNECTION_ERROR_OCCURRED");
            break;
        case SessionEvent.SESSION_LOGGED_IN:
            sb.append("CONNECTION_LOGGED_IN");
            break;
        case SessionEvent.SESSION_LOGGED_OFF:
            sb.append("CONNECTION_LOGGED_OFF");
            break;
        case SessionEvent.SESSION_OPENED:
            sb.append("CONNECTION_OPENED");
            break;
        case SessionEvent.SESSION_OPENING:
            sb.append("CONNECTION_OPENING");
            break;
        case SessionEvent.SESSION_CONNECTION_REFUSED:
            sb.append("CONNECTION_CONNECTION_REFUSED");
            break;
        default:
            sb.append(eventType);
        }
        sb.append("|");

        sb.append(this.getGitApi().getRepository()).append("|");
        sb.append(this.source);

        if (exception != null) {
            sb.append("|");
            sb.append(exception.getClass().getName()).append(":");
            sb.append(exception.getMessage());
        }

        sb.append("]");

        return sb.toString();
    }
}
