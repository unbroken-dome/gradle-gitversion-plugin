package org.unbrokendome.gradle.plugins.gitversion.core;

import org.eclipse.jgit.lib.Constants;
import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.internal.GitOperations;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.HasObjectId;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;


public interface RuleContext {

    MutableSemVersion getVersion();

    /**
     * Gets the Gradle project.
     *
     * @return the Gradle {@link Project}
     */
    @Nonnull
    Project getProject();

    /**
     * Gets the Git repository.
     *
     * @return the {@link GitRepository}
     */
    @Nonnull
    GitRepository getRepository();

    /**
     * Gets the current HEAD commit.
     *
     * @return the current HEAD as a {@link GitCommit}, or {@code null} if the repository is empty
     */
    @Nullable
    default GitCommit getHead() {
        return getRepository().getHead();
    }

    boolean isSkipOtherRules();

    void setSkipOtherRules(boolean skipOtherRules);

    @Nullable
    default GitBranch getCurrentBranch() {
        return getRepository().getCurrentBranch();
    }

    /**
     * Gets the <em>short</em> name of the current branch, i.e. the branch that the HEAD points to.
     * Returns {@code null} if the repository is in "detached head" state.
     *
     * <p>The short branch name does not include the {@code refs/heads/} prefix.</p>
     *
     * @return the short name of the current branch
     */
    @Nullable
    default String getBranchName() {
        GitBranch branch = getCurrentBranch();
        return branch != null ? branch.getShortName() : null;
    }


    @Nullable
    default BranchPoint branchPoint() {
        String masterBranch = Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + "/" + Constants.MASTER;
        return branchPoint(masterBranch);
    }


    @Nullable
    default BranchPoint branchPoint(String... otherBranchNames) {
        return new GitOperations(getRepository()).branchPoint(otherBranchNames);
    }


    @Nullable
    default BranchPoint branchPoint(Pattern otherBranchNamePattern) {
        return new GitOperations(getRepository()).branchPoint(otherBranchNamePattern);
    }


    default int countCommitsSince(HasObjectId obj) {
        return new GitOperations(getRepository()).countCommitsSince(obj);
    }


    @Nullable
    default TaggedCommit findLatestTag(Pattern tagNamePattern, boolean includeMerges) {
        return new GitOperations(getRepository()).findLatestTag(tagNamePattern, includeMerges);
    }


    @Nullable
    default TaggedCommit findLatestTag(Pattern tagNamePattern) {
        return findLatestTag(tagNamePattern, false);
    }
}
