package com.reversecoder.git.api;

import java.io.OutputStream;

import com.reversecoder.git.api.resource.Resource;

public class OutputData {
    private OutputStream outputStream;

    private Resource resource;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
