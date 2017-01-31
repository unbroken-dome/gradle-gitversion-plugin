package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;

public interface GitTag {

    @Nonnull
    String getName();

    @Nonnull
    GitCommit getTarget();
}
