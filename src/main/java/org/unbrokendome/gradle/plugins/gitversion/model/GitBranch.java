package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;


public interface GitBranch {

    @Nonnull
    String getShortName();

    @Nonnull
    String getFullName();

    @Nonnull
    GitCommit getHead();
}
