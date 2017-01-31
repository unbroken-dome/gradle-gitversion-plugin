package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;

public class MockGitTag implements GitTag {

    private final String name;
    private final GitCommit target;

    public MockGitTag(String name, GitCommit target) {
        this.name = name;
        this.target = target;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public GitCommit getTarget() {
        return target;
    }
}
