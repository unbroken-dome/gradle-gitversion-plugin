package org.unbrokendome.gradle.plugins.gitversion.model;

public interface CloseableGitRepository extends GitRepository, AutoCloseable {

    @Override
    void close();
}
