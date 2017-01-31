package org.unbrokendome.gradle.plugins.gitversion.model;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MockGitRepositoryBuilder {

    private final Map<String, MockGitCommit> branchHeads = new HashMap<>();
    private final Map<String, MockGitTag> tags = new HashMap<>();

    @Nonnull
    private File workingDir = Paths.get("").toAbsolutePath().toFile();

    @Nullable
    private String currentBranch = "refs/heads/master";

    @Nullable
    private MockGitCommit head;

    @Nonnull
    public MockGitRepositoryBuilder setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    @Nonnull
    public MockGitRepositoryBuilder checkoutNew(String shortName) {
        String fullName = "refs/heads/" + shortName;
        branchHeads.put(fullName, head);
        currentBranch = fullName;
        return this;
    }

    @Nonnull
    public MockGitRepositoryBuilder checkout(String shortName) {
        String fullName = "refs/heads/" + shortName;

        MockGitCommit branchHead = branchHeads.get(fullName);
        if (branchHead == null) {
            throw new IllegalArgumentException("No such branch: \"" + fullName + "\"");
        }

        currentBranch = fullName;
        head = branchHead;

        return this;
    }


    @Nonnull
    public MockGitRepositoryBuilder trackRemote(String remoteName) {
        String remoteTrackingBranchName = getRemoteTrackingBranchName(remoteName);
        branchHeads.put(remoteTrackingBranchName, head);
        return this;
    }


    @Nonnull
    public MockGitRepositoryBuilder trackRemote() {
        return trackRemote("origin");
    }


    @Nonnull
    public MockGitRepositoryBuilder checkoutRemote(String remoteName) {
        trackRemote(remoteName);
        currentBranch = getRemoteTrackingBranchName(remoteName);
        return this;
    }


    @Nonnull
    private String getRemoteTrackingBranchName(String remoteName) {
        if (currentBranch == null || !currentBranch.startsWith("refs/heads/")) {
            throw new IllegalStateException("Must have a local branch checked out to update remote tracking branch");
        }

        String shortBranchName = currentBranch.substring("refs/heads/".length());
        return "refs/remotes/" + remoteName + "/" + shortBranchName;
    }


    @Nonnull
    public MockGitRepositoryBuilder checkoutRemote() {
        return checkoutRemote("origin");
    }


    @Nonnull
    public MockGitRepositoryBuilder commit(String message) {

        if (currentBranch == null) {
            throw new IllegalStateException("Cannot commit when in detached head mode");
        }

        List<GitCommit> parents = head != null ? ImmutableList.of(head) : ImmutableList.of();
        head = new MockGitCommit(parents, message);
        branchHeads.put(currentBranch, head);

        return this;
    }


    @Nonnull
    public MockGitRepositoryBuilder merge(String fromBranch, String message) {

        if (head == null) {
            throw new IllegalStateException("Need at least 1 commit on this branch to merge");
        }
        if (currentBranch == null) {
            throw new IllegalStateException("Cannot merge when in detached head mode");
        }

        String fullBranchName;
        if (fromBranch.startsWith("refs/heads/") || fromBranch.startsWith("refs/remotes/")) {
            fullBranchName = fromBranch;
        } else {
            fullBranchName = "refs/heads/" + fromBranch;
        }

        MockGitCommit mergedBranchHead = branchHeads.get(fullBranchName);
        if (mergedBranchHead == null) {
            throw new IllegalArgumentException("Branch \"" + fullBranchName + "\" does not exist");
        }

        List<GitCommit> parents = ImmutableList.of(head, mergedBranchHead);
        head = new MockGitCommit(parents, message);
        branchHeads.put(currentBranch, head);

        return this;
    }


    @Nonnull
    public MockGitRepositoryBuilder merge(String fromBranch) {
        return merge(fromBranch, "Merge from " + fromBranch);
    }


    @Nonnull
    public MockGitRepositoryBuilder tag(String tagName) {
        if (head == null) {
            throw new IllegalStateException("Need at least 1 commit on this branch to merge");
        }

        MockGitTag tag = new MockGitTag(tagName, head);
        head.addTag(tag);
        tags.put(tagName, tag);

        return this;
    }


    @Nonnull
    public MockGitRepositoryBuilder detach() {
        currentBranch = null;
        return this;
    }


    @Nonnull
    public MockGitRepository build() {
        Map<String, GitBranch> branches = Maps.transformEntries(branchHeads, MockGitBranch::new);
        return new MockGitRepository(workingDir, branches, tags, currentBranch, head);
    }
}
