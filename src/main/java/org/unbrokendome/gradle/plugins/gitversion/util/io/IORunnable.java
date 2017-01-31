package org.unbrokendome.gradle.plugins.gitversion.util.io;

import java.io.IOException;


@FunctionalInterface
public interface IORunnable {

    void run() throws IOException;
}
