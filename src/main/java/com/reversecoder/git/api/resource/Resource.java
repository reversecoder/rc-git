package com.reversecoder.git.api.resource;

import com.reversecoder.git.api.GitApiConstants;

public class Resource {
    private String name;

    private long lastModified;

    private long contentLength = GitApiConstants.UNKNOWN_LENGTH;

    public Resource() {

    }

    public Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the value of the last-modified header field. The result is the
     * number of milliseconds since January 1, 1970 GMT.
     *
     * @return the date the resource was last modified, or
     *         GitApiConstants.UNKNOWN_LENGTH if not known.
     */
    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String toString() {
        return name;
    }

    public String inspect() {
        return name + "[len = " + contentLength + "; mod = " + lastModified + "]";
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (contentLength ^ (contentLength >>> 32));
        result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Resource other = (Resource) obj;
        if (contentLength != other.contentLength) {
            return false;
        }
        if (lastModified != other.lastModified) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
