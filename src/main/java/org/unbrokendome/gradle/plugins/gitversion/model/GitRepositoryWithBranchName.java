package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;


public final class GitRepositoryWithBranchName implements CloseableGitRepository {

    private final CloseableGitRepository gitRepository;

    @Nullable
    private final String branchName;


    public GitRepositoryWithBranchName(CloseableGitRepository gitRepository, @Nullable String branchName) {
        this.gitRepository = gitRepository;
        this.branchName = branchName;
    }


    @Override
    public File getWorkingDir() {
        return gitRepository.getWorkingDir();
    }


    @Nullable
    @Override
    public GitCommit getHead() {
        return gitRepository.getHead();
    }


    @Nullable
    @Override
    public GitBranch getCurrentBranch() {
        return branchName != null ? gitRepository.getBranch(branchName) : null;
    }


    @Nullable
    @Override
    public GitBranch getBranch(String name) {
        return gitRepository.getBranch(name);
    }


    @Nonnull
    @Override
    public Collection<? extends GitBranch> getBranches() {
        return gitRepository.getBranches();
    }


    @Nonnull
    @Override
    public CloseableIterator<GitCommit> walk(GitCommit startCommit, WalkMode mode) {
        return gitRepository.walk(startCommit, mode);
    }


    @Override
    public Collection<? extends GitTag> getTags() {
        return gitRepository.getTags();
    }


    @Override
    public void close() {
        gitRepository.close();
    }
}
