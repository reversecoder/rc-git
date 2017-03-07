package com.reversecoder.git.api.events;

import java.util.EventObject;

import com.reversecoder.git.api.GitApi;

public class GitApiEvent extends EventObject {
    /**
     * The time when event occurred
     */
    protected long timestamp;

    /**
     * @param source
     *            The git api object on which the GitApiEvent initially occurred
     */
    public GitApiEvent(final GitApi source) {
        super(source);
    }

    /**
     * Returns The git api object on which the GitApiEvent initially occurred
     *
     * @return The git api object on which the GitApiEvent initially occurred
     */
    public GitApi getGitApi() {
        return (GitApi) getSource();
    }

    /**
     * Returns the timestamp which indicated the time when this event has
     * occurred
     *
     * @return Returns the timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp which indicated the time when this event has occurred
     *
     * @param timestamp
     *            The timestamp to set.
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

}
