package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.util.function.Supplier;

import org.unbrokendome.gradle.plugins.gitversion.util.Lazy;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;


/**
 * A "lazy" version that will be evaluated only if its {@link #toString()} method is called.
 */
public final class LazyVersion {

    private final Lazy<SemVersion> version;


    public LazyVersion(Supplier<SemVersion> versionSupplier) {
        this.version = Lazy.of(versionSupplier);
    }


    @Override
    public String toString() {
        SemVersion actualVersion = version.get();
        return actualVersion != null ? version.toString() : null;
    }
}
