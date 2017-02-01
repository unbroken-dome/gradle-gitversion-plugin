package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Set;


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
        return branchName != null ? getBranch(branchName) : null;
    }


    @Nullable
    @Override
    public GitBranch getBranch(String name) {

        // Check if the underlying repository can find the branch
        GitBranch branch = gitRepository.getBranch(name);
        if (branch != null) {
            return branch;
        }

        if (!name.startsWith("refs/heads/")) {
            // Try the remote-tracking branches for each remote (e.g. origin/master)
            for (String remoteName : getRemoteNames()) {
                String remoteBranchCandidate = "refs/remotes/" + remoteName + "/" + name;
                branch = gitRepository.getBranch(remoteBranchCandidate);
                if (branch != null) {
                    return branch;
                }
            }
        }

        return null;
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


    @Nonnull
    @Override
    public Collection<? extends GitTag> getTags() {
        return gitRepository.getTags();
    }


    @Nonnull
    @Override
    public Set<String> getRemoteNames() {
        return gitRepository.getRemoteNames();
    }


    @Override
    public void close() {
        gitRepository.close();
    }
}
