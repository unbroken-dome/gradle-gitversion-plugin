package org.unbrokendome.gradle.plugins.gitversion.util.io;

import java.io.IOException;


@FunctionalInterface
public interface IOFunction<T, R> {

    R apply(T t) throws IOException;
}
