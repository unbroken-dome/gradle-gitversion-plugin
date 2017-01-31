package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;


public interface GitRepository extends AutoCloseable {

    File getWorkingDir();

    @Nullable
    GitCommit getHead();

    @Nullable
    GitBranch getCurrentBranch();

    @Nullable
    GitBranch getBranch(String name);

    @Nonnull
    Collection<? extends GitBranch> getBranches();

    @Nonnull
    Collection<? extends GitTag> getTags();

    @Nonnull
    CloseableIterator<GitCommit> walk(GitCommit startCommit, WalkMode mode);

    default CloseableIterator<GitCommit> walk(GitCommit startCommit) {
        return walk(startCommit, WalkMode.ALL);
    }

    default CloseableIterator<GitCommit> walk(WalkMode walkMode) {
        GitCommit head = getHead();
        if (head == null) {
            return CloseableIterator.emptyIterator();
        }
        return walk(getHead(), walkMode);
    }

    default CloseableIterator<GitCommit> walk() {
        return walk(WalkMode.ALL);
    }

    default CloseableIterator<GitCommit> firstParentWalk(GitCommit startCommit) {
        return walk(startCommit, WalkMode.FIRST_PARENT_ONLY);
    }

    default CloseableIterator<GitCommit> firstParentWalk() {
        return walk(WalkMode.FIRST_PARENT_ONLY);
    }

    enum WalkMode {
        ALL,
        FIRST_PARENT_ONLY
    }
}
