package org.unbrokendome.gradle.plugins.gitversion.model;

import java.util.Iterator;

public class CloseableIteratorImpl<E> implements CloseableIterator<E> {

    private final Iterator<E> iterator;

    public CloseableIteratorImpl(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        return iterator.next();
    }

    @Override
    public void close() {
    }
}
