package org.unbrokendome.gradle.plugins.gitversion.model;

import java.util.Iterator;
import java.util.NoSuchElementException;


public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {

    // do not throw checked Exceptions
    @Override
    void close();

    static <E> CloseableIterator<E> emptyIterator() {
        return new CloseableIterator<E>() {
            @Override
            public void close() {
            }


            @Override
            public boolean hasNext() {
                return false;
            }


            @Override
            public E next() {
                throw new NoSuchElementException();
            }
        };
    }
}
