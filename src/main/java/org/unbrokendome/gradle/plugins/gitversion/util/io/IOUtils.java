package org.unbrokendome.gradle.plugins.gitversion.util.io;

import java.io.IOException;


public final class IOUtils {

    private IOUtils() { }

    public static <T> T unchecked(IOCallable<T> callable) {
        try {
            return callable.call();
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public static void unchecked(IORunnable runnable) {
        try {
            runnable.run();
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }
}
