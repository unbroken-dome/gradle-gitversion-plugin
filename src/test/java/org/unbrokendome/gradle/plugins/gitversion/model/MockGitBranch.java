package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;


public class MockGitBranch implements GitBranch {

    private static final String REFS_HEADS = "refs/heads/";
    private static final String REFS_REMOTES = "refs/remotes/";

    private final String fullName;
    private final String shortName;
    private final GitCommit head;


    public MockGitBranch(String fullName, GitCommit head) {
        this.fullName = fullName;
        this.head = head;

        if (fullName.startsWith(REFS_HEADS)) {
            shortName = fullName.substring(REFS_HEADS.length());
        } else if (fullName.startsWith(REFS_REMOTES)) {
            shortName = fullName.substring(REFS_REMOTES.length());
        } else {
            throw new IllegalArgumentException("Illegal branch name: \"" + fullName + "\"");
        }
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
