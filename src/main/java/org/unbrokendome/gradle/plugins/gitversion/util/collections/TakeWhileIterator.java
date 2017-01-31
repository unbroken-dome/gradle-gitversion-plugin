package org.unbrokendome.gradle.plugins.gitversion.util.collections;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;
import java.util.function.Predicate;


public class TakeWhileIterator<E> extends AbstractIterator<E> {

    private final Iterator<E> iterator;
    private final Predicate<E> predicate;


    public TakeWhileIterator(Iterator<E> iterator, Predicate<E> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }


    @Override
    protected E computeNext() {
        if (iterator.hasNext()) {
            E item = iterator.next();
            if (predicate.test(item)) {
                return item;
            }
        }
        endOfData();
        return null;
    }
}
