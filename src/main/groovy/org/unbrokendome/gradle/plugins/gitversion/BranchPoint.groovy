package org.unbrokendome.gradle.plugins.gitversion

import org.eclipse.jgit.lib.ObjectId

import java.util.regex.Matcher

/**
 * Represents a "branch point", i.e. a point in the commit history where one branch
 * was pulled off another.
 */
class BranchPoint implements HasObjectId {

    /**
     * The name of the branch that was created.
     */
    final String branchName

    /**
     * The commit from which the branch was created.
     */
    final GitCommit commit

    /**
     * If the branch point was found based on a regular expression match, contains the
     * capture groups for the branch name.
     */
    final MatcherFacade matches

    BranchPoint(String branchName,
                ObjectId commitId,
                Matcher matcher = null) {
        this.branchName = branchName
        this.commit = new GitCommit(commitId)
        this.matches = (matcher != null) ? new DefaultMatcherFacade(matcher) : NullMatcherFacade.INSTANCE
    }

    @Override
    ObjectId getObjectId() {
        commit.objectId
    }
}
