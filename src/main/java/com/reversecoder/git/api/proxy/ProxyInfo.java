package com.reversecoder.git.api.proxy;

import com.reversecoder.git.api.GitApiConstants;

import java.io.Serializable;

public class ProxyInfo implements Serializable {
    public static final String PROXY_SOCKS5 = "SOCKS_5";

    public static final String PROXY_SOCKS4 = "SOCKS4";

    public static final String PROXY_HTTP = "HTTP";

    /**
     * Proxy server host
     */
    private String host = null;

    /**
     * Username used to access the proxy server
     */
    private String userName = null;

    /**
     * Password associated with the proxy server
     */
    private String password = null;

    /**
     * Proxy server port
     */
    private int port = GitApiConstants.UNKNOWN_PORT;

    /**
     * Type of the proxy
     */
    private String type = null;

    /**
     * The non-proxy hosts. Follows Java system property format:
     * <code>*.foo.com|localhost</code>.
     */
    private String nonProxyHosts;

    /**
     * For NTLM proxies, specifies the NTLM host.
     */
    private String ntlmHost;

    /**
     * For NTLM proxies, specifies the NTLM domain.
     */
    private String ntlmDomain;

    /**
     * Return proxy server host name.
     *
     * @return proxy server host name
     */
    public String getHost() {
        return host;
    }

    /**
     * Set proxy host name.
     *
     * @param host
     *            proxy server host name
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Get user's password used to login to proxy server.
     *
     * @return user's password at proxy host
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the user's password for the proxy server.
     *
     * @param password
     *            password to use to login to a proxy server
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get the proxy port.
     *
     * @return proxy server port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the proxy port.
     *
     * @param port
     *            proxy server port
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Get the proxy username.
     *
     * @return username for the proxy server
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the proxy username.
     *
     * @param userName
     *            username for the proxy server
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    /**
     * Get the type of the proxy server.
     *
     * @return the type of the proxy server
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type of the proxy server like <i>SOCKSv4</i>
     */
    public void setType(final String type) {
        this.type = type;
    }

    public String getNonProxyHosts() {
        return nonProxyHosts;
    }

    public void setNonProxyHosts(String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }

    public String getNtlmHost() {
        return ntlmHost;
    }

    public void setNtlmHost(String ntlmHost) {
        this.ntlmHost = ntlmHost;
    }

    public void setNtlmDomain(String ntlmDomain) {
        this.ntlmDomain = ntlmDomain;
    }

    public String getNtlmDomain() {
        return ntlmDomain;
    }

    @Override
    public String toString() {
        return "ProxyInfo{" + "host='" + host + '\'' + ", userName='" + userName + '\'' + ", port="
                + port + ", type='" + type + '\'' + ", nonProxyHosts='" + nonProxyHosts + '\''
                + '}';
    }
}
