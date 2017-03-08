package com.reversecoder.git.api.proxy;

public interface ProxyInfoProvider {
    /**
     * Returns the proxy settings for the given protocol.
     * 
     * @param protocol The protocol.
     * @return Proxy settings or null, if no proxy is configured for this
     *         protocol.
     */
    ProxyInfo getProxyInfo(String protocol);
}
