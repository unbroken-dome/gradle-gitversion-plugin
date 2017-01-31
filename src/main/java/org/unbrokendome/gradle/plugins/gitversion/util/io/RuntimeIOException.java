package org.unbrokendome.gradle.plugins.gitversion.util.io;

import java.io.IOException;


public final class RuntimeIOException extends RuntimeException {

    public RuntimeIOException(IOException cause) {
        super(cause);
    }
}
