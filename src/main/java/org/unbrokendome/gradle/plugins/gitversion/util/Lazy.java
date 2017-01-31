package org.unbrokendome.gradle.plugins.gitversion.util;

import java.util.function.Supplier;


public class Lazy<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private transient volatile boolean initialized = false;
    private transient T value;


    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }


    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }


    @Override
    public T get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    T t = supplier.get();
                    value = t;
                    initialized = true;
                    return t;
                }
            }
        }
        return value;
    }
}
