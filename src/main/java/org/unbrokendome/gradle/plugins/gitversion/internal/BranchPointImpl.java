package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.unbrokendome.gradle.plugins.gitversion.core.BranchPoint;
import org.unbrokendome.gradle.plugins.gitversion.core.MatcherFacade;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;


public class BranchPointImpl implements BranchPoint {

    private final GitCommit commit;
    private final GitBranch otherBranch;
    private final MatcherFacade matches;


    BranchPointImpl(GitCommit commit, GitBranch otherBranch) {
        this(commit, otherBranch, NullMatcherFacade.INSTANCE);
    }


    BranchPointImpl(GitCommit commit, GitBranch otherBranch, MatcherFacade matches) {
        this.commit = commit;
        this.otherBranch = otherBranch;
        this.matches = matches;
    }


    @Override
    public GitCommit getCommit() {
        return commit;
    }


    @Override
    public GitBranch getOtherBranch() {
        return otherBranch;
    }


    @Override
    public MatcherFacade getMatches() {
        return matches;
    }
}
