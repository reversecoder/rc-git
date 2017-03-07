package com.reversecoder.git.api;

import org.codehaus.plexus.util.FileUtils;

import com.reversecoder.git.api.authorization.AuthorizationException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public final class GitApiUtils {
    private GitApiUtils() {
    }

    public static String toString(String resource, GitApi gitApi) throws IOException,
            TransferFailedException, ResourceDoesNotExistException, AuthorizationException {

        File file = null;

        try {
            file = File.createTempFile("rc-git", "tmp");

            gitApi.get(resource, file);

            return FileUtils.fileRead(file);
        } finally {
            if (file != null) {
                boolean deleted = file.delete();

                if (!deleted) {
                    file.deleteOnExit();
                }
            }
        }

    }

    public static void putDirectory(File dir, GitApi gitApi, boolean includeBasdir)
            throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {

        LinkedList queue = new LinkedList();

        if (includeBasdir) {
            queue.add(dir.getName());
        } else {
            queue.add("");
        }

        while (!queue.isEmpty()) {
            String path = (String) queue.removeFirst();

            File currentDir = new File(dir, path);

            File[] files = currentDir.listFiles();

            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                String resource;

                if (path.length() > 0) {
                    resource = path + "/" + file.getName();
                } else {
                    resource = file.getName();
                }

                if (file.isDirectory()) {
                    queue.add(resource);
                } else {
                    gitApi.put(file, resource);
                }

            }

        }

    }
}
