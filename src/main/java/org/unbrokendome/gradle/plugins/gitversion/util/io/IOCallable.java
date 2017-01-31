package org.unbrokendome.gradle.plugins.gitversion.util.io;

import java.io.IOException;


@FunctionalInterface
public interface IOCallable<T> {

    T call() throws IOException;
}
