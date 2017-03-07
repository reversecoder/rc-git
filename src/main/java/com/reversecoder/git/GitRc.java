package com.reversecoder.git;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.scm.log.ScmLogger;
import com.reversecoder.git.api.ConnectionException;
import com.reversecoder.git.api.InputData;
import com.reversecoder.git.api.LazyFileOutputStream;
import com.reversecoder.git.api.OutputData;
import com.reversecoder.git.api.ResourceDoesNotExistException;
import com.reversecoder.git.api.StreamGitApi;
import com.reversecoder.git.api.TransferFailedException;
import com.reversecoder.git.api.authentication.AuthenticationException;
import com.reversecoder.git.api.authorization.AuthorizationException;
import com.reversecoder.git.api.observers.Debug;
import com.reversecoder.git.api.resource.Resource;
import org.codehaus.plexus.util.FileUtils;

public class GitRc extends StreamGitApi {

    private final boolean debug = Utils.getBooleanEnvironmentProperty("rc.git.debug");
    private final boolean safeCheckout = Utils
            .getBooleanEnvironmentProperty("rc.git.safe.checkout");
    private final boolean skipEmptyCommit = Utils
            .getBooleanEnvironmentProperty("rc.git.skip.empty.commit");

    private final ScmLogger log = new GitRcLog(debug);

    private GitBackend git = null;

    public GitRc() {

        if (debug) {
            Debug d = new Debug();
            addSessionListener(d);
            addTransferListener(d);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void fillInputData(InputData inputData)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {

        log.debug("Invoked fillInputData()");

        Resource resource = inputData.getResource();

        File file = new File(git.workDir, resource.getName());

        if (!file.exists()) {
            throw new ResourceDoesNotExistException("File: " + file + " does not exist");
        }

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));

            inputData.setInputStream(in);

            resource.setContentLength(file.length());

            resource.setLastModified(file.lastModified());
        } catch (FileNotFoundException e) {
            throw new TransferFailedException("Could not read from file: " + file.getAbsolutePath(),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fillOutputData(OutputData outputData) throws TransferFailedException {

        log.debug("Invoked fillOutputData()");

        Resource resource = outputData.getResource();

        File file = new File(git.workDir, resource.getName());

        createParentDirectories(file);

        OutputStream outputStream = new BufferedOutputStream(new LazyFileOutputStream(file));

        outputData.setOutputStream(outputStream);
    }

    /**
     * {@inheritDoc}
     */
    protected void openConnectionInternal() throws ConnectionException, AuthenticationException {

        log.debug("Invoked openConnectionInternal()");

        if (git == null) {
            try {

                String url = getRepository().getUrl();

                if (url.endsWith("/"))
                    url = url.substring(0, url.length() - 1);

                String remote;
                String branch;

                url = url.substring("git:".length());
                int i = url.indexOf(':');
                if (i < 0) {
                    remote = url;
                    branch = "master";
                } else {
                    branch = url.substring(0, i);
                    remote = url.substring(i + 3, url.length());
                }

                File workDir = Utils.createCheckoutDirectory(remote);

                if (!workDir.exists() || !workDir.isDirectory() || !workDir.canWrite())
                    throw new ConnectionException("Unable to create working directory");

                if (safeCheckout)
                    FileUtils.cleanDirectory(workDir);

                git = new GitBackend(workDir, remote, branch, log);
                git.pullAll();
            } catch (Exception e) {
                throw new ConnectionException("Unable to pull git repository: " + e.getMessage(),
                        e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void closeConnection() throws ConnectionException {

        log.debug("Invoked closeConnection()");

        try {

            git.pushAll(skipEmptyCommit);

            if (safeCheckout)
                FileUtils.cleanDirectory(git.workDir);

        } catch (Exception e) {
            throw new ConnectionException("Unable to push git repostory: " + e.getMessage(), e);
        }
    }
}
