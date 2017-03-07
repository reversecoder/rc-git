package com.reversecoder.git.api;

import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {
    public static String loadProperty(String name) throws IOException {
        return loadProperty(name, System.getProperty("application.properties",
                GitApiConstants.RC_GIT_PROPERTIES));
    }

    public static String loadProperty(String name, String fromResource) throws IOException {
        Properties props = new Properties();
        props.load(PropertyLoader.class.getResourceAsStream(fromResource));

        return props.getProperty(name);
    }
}