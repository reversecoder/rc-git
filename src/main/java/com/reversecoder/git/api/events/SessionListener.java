package com.reversecoder.git.api.events;

public interface SessionListener {

    /**
     * This method will be called when Wagon is about to open connection to the
     * repository. The type of the event should be set to
     * {@link SessionEvent#SESSION_OPENING}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionOpening(SessionEvent sessionEvent);

    /**
     * This method will be called when git api has successfully connected to to
     * the repository. The type of the event should be set to
     * {@link SessionEvent#SESSION_OPENED}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionOpened(SessionEvent sessionEvent);

    /**
     * This method will be called when Wagon has closed connection to to the
     * repository. The type of the event should be set to
     * {@link SessionEvent#SESSION_DISCONNECTING}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionDisconnecting(SessionEvent sessionEvent);

    /**
     * This method will be called when Wagon has closed connection to the
     * repository. The type of the event should be set to
     * {@link SessionEvent#SESSION_DISCONNECTED}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionDisconnected(SessionEvent sessionEvent);

    /**
     * This method will be called when Wagon when connection to the repository
     * was refused.
     *
     * The type of the event should be set to
     * {@link SessionEvent#SESSION_CONNECTION_REFUSED}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionConnectionRefused(SessionEvent sessionEvent);

    /**
     * This method will be called by Wagon when Wagon managed to login to the
     * repository.
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionLoggedIn(SessionEvent sessionEvent);

    /**
     * This method will be called by Wagon has logged off from the repository.
     *
     * The type of the event should be set to
     * {@link SessionEvent#SESSION_LOGGED_OFF}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionLoggedOff(SessionEvent sessionEvent);

    /**
     * This method will be called by Wagon when an error occurred.
     *
     * The type of the event should be set to
     * {@link SessionEvent#SESSION_ERROR_OCCURRED}
     *
     * @param sessionEvent
     *            the session event
     */
    void sessionError(SessionEvent sessionEvent);

    /**
     * This method allows to send arbitrary debug messages.
     *
     * @param message
     *            the debug message
     */
    void debug(String message);

}