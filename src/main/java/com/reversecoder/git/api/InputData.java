package com.reversecoder.git.api;

import java.io.InputStream;

import com.reversecoder.git.api.resource.Resource;

public class InputData {
    private InputStream inputStream;

    private Resource resource;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
