package com.reversecoder.git.api.proxy;

import java.util.StringTokenizer;

public final class ProxyUtils {
    private ProxyUtils() {
    }

    /**
     * Check if the specified host is in the list of non proxy hosts.
     * 
     * @param proxy
     *            the proxy info object contains set of properties.
     * @param targetHost
     *            the target hostname
     * @return true if the hostname is in the list of non proxy hosts, false
     *         otherwise.
     */
    public static boolean validateNonProxyHosts(ProxyInfo proxy, String targetHost) {
        if (targetHost == null) {
            targetHost = new String();
        }
        if (proxy == null) {
            return false;
        }
        String nonProxyHosts = proxy.getNonProxyHosts();
        if (nonProxyHosts == null) {
            return false;
        }

        StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|");

        while (tokenizer.hasMoreTokens()) {
            String pattern = tokenizer.nextToken();
            pattern = pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
            if (targetHost.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}
