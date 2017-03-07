package com.reversecoder.git.api.repository;

import org.codehaus.plexus.util.StringUtils;

import com.reversecoder.git.api.PathUtils;
import com.reversecoder.git.api.GitApiConstants;

import java.io.Serializable;
import java.util.Properties;

public class Repository implements Serializable {
    private static final long serialVersionUID = 1312227676322136247L;

    private String id;

    private String name;

    private String host;

    private int port = GitApiConstants.UNKNOWN_PORT;

    private String basedir;

    private String protocol;

    private String url;

    private RepositoryPermissions permissions;

    /**
     * Properties influencing git api behaviour which are very specific to
     * particular wagon.
     */
    private Properties parameters = new Properties();

    // Username/password are sometimes encoded in the URL
    private String username = null;

    private String password = null;

    /**
     * @deprecated use {@link #Repository(String, String)}
     */
    public Repository() {

    }

    public Repository(String id, String url) {
        if (id == null) {
            throw new NullPointerException("id cannot be null");
        }

        setId(id);

        if (url == null) {
            throw new NullPointerException("url cannot be null");
        }

        setUrl(url);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieve the base directory of the repository. This is derived from the
     * full repository URL, and contains the entire path component.
     *
     * @return the base directory
     */
    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUrl(String url) {
        this.url = url;

        // TODO [BP]: refactor out the PathUtils URL stuff into a class like
        // java.net.URL, so you only parse once
        // can't use URL class as is because it won't recognise our protocols,
        // though perhaps we could attempt to
        // register handlers for scp, etc?

        this.protocol = PathUtils.protocol(url);

        this.host = PathUtils.host(url);

        this.port = PathUtils.port(url);

        this.basedir = PathUtils.basedir(url);

        String username = PathUtils.user(url);
        this.username = username;

        if (username != null) {
            String password = PathUtils.password(url);

            if (password != null) {
                this.password = password;

                username += ":" + password;
            }

            username += "@";

            int index = url.indexOf(username);
            this.url = url.substring(0, index) + url.substring(index + username.length());
        }
    }

    public String getUrl() {
        if (url != null) {
            return url;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(protocol);

        sb.append("://");

        if (isIPv6Address()) {
            // If this is IPv6 then we have to surround it
            // with brackets '[' and ']'
            sb.append("[").append(getHost()).append("]");
        } else {
            sb.append(getHost());
        }

        if (port != GitApiConstants.UNKNOWN_PORT) {
            sb.append(":");

            sb.append(port);
        }

        sb.append(basedir);

        return sb.toString();
    }

    /**
     * Checks whtther provided url contains IPv6 format in host portion
     *
     * @return true if provide host part is of IPv6 format
     */
    private boolean isIPv6Address() {
        return getHost().contains(":");
    }

    public String getHost() {
        if (host == null) {
            return "localhost";
        }
        return host;
    }

    public String getName() {
        if (name == null) {
            return getId();
        }
        return name;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Repository[");

        if (StringUtils.isNotEmpty(getName())) {
            sb.append(getName()).append("|");
        }

        sb.append(getUrl());
        sb.append("]");

        return sb.toString();
    }

    public String getProtocol() {
        return protocol;
    }

    public RepositoryPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(RepositoryPermissions permissions) {
        this.permissions = permissions;
    }

    public String getParameter(String key) {
        return parameters.getProperty(key);
    }

    public void setParameters(Properties parameters) {
        this.parameters = parameters;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        final Repository other = (Repository) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
