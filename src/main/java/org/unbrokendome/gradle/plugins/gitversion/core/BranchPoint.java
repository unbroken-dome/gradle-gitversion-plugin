package org.unbrokendome.gradle.plugins.gitversion.core;

import org.unbrokendome.gradle.plugins.gitversion.internal.GitOperations;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.HasObjectId;

import javax.annotation.Nonnull;


/**
 * A point in the commit history where a branch was created off another branch.
 *
 * This is used for the result of the {@link GitOperations#branchPoint} methods.
 */
public interface BranchPoint extends HasObjectId {

    /**
     * Gets the commit from which the branch was created.
     * @return the Git commit
     */
    @Nonnull
    GitCommit getCommit();

    /**
     * Gets the other branch which was forked off here.
     * @return the other branch
     */
    @Nonnull
    GitBranch getOtherBranch();

    /**
     * Gets the name of the other branch which was forked off here.
     * @return the (short) name of the other branch
     */
    @Nonnull
    default String getOtherBranchName() {
        return getOtherBranch().getShortName();
    }

    /**
     * If a regular expression pattern was used to find this branch point, gets the matches
     * from the branch name.
     * @return the matches
     */
    @Nonnull
    MatcherFacade getMatches();

    /**
     * Gets the commit ID.
     * @return the commit ID
     */
    @Nonnull
    @Override
    default String getId() {
        return getCommit().getId();
    }
}
