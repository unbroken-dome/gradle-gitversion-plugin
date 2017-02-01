package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableList;


public final class GitRepositoryWithBranchName implements CloseableGitRepository {

    private static final String REFS_HEADS = "refs/heads/";
    private static final String REFS_REMOTES = "refs/remotes/";


    private final CloseableGitRepository gitRepository;

    @Nullable
    private final String branchName;
    @Nullable
    private final GitBranch fakeBranch;


    public GitRepositoryWithBranchName(CloseableGitRepository gitRepository, @Nullable String branchName) {
        this.gitRepository = gitRepository;
        this.branchName = branchName;
        this.fakeBranch = createFakeGitBranchIfNeeded();
    }


    @Nullable
    private GitBranch createFakeGitBranchIfNeeded() {
        if (branchName != null) {
            // Check if the given branch name is actually contained in the underlying repo
            GitBranch branch = gitRepository.getBranch(branchName);

            GitCommit head = gitRepository.getHead();

            if (branch == null && head != null) {
                String shortName, fullName;
                if (branchName.startsWith(REFS_HEADS)) {
                    shortName = branchName.substring(REFS_HEADS.length());
                    fullName = branchName;
                } else if (branchName.startsWith(REFS_REMOTES)) {
                    shortName = branchName.substring(REFS_REMOTES.length());
                    fullName = branchName;
                } else {
                    shortName = branchName;
                    fullName = REFS_HEADS + branchName;
                }
                return new FakeGitBranch(shortName, fullName, head);
            }
        }

        return null;
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

        if (fakeBranch != null &&
                (fakeBranch.getShortName().equals(name) || fakeBranch.getFullName().equals(name))) {
            return fakeBranch;
        }

        return gitRepository.getBranch(name);
    }


    @Nonnull
    @Override
    public Collection<? extends GitBranch> getBranches() {

        Collection<? extends GitBranch> branches = gitRepository.getBranches();

        if (fakeBranch != null) {
            return ImmutableList.<GitBranch> builder()
                    .add(fakeBranch)
                    .addAll(branches)
                    .build();
        } else {
            return branches;
        }
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


    private static final class FakeGitBranch implements GitBranch {

        private final String shortName;
        private final String fullName;
        private final GitCommit head;


        private FakeGitBranch(String shortName, String fullName, GitCommit head) {
            this.shortName = shortName;
            this.fullName = fullName;
            this.head = head;
        }


        @Nonnull
        @Override
        public String getShortName() {
            return shortName;
        }

        @Nonnull
        @Override
        public String getFullName() {
            return fullName;
        }

        @Nonnull
        @Override
        public GitCommit getHead() {
            return head;
        }
    }
}
