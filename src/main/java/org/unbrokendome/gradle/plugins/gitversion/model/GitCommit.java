package org.unbrokendome.gradle.plugins.gitversion.model;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;


public interface GitCommit extends HasObjectId {

    @Nonnull
    String getMessage();

    /**
     * Gets a collection of all tags in the repository that point to this commit.
     *
     * @return a collection of {@link GitTag} objects (may be empty)
     */
    @Nonnull
    Collection<? extends GitTag> getTags();

    /**
     * Gets a list of the parent commits.
     *
     * <p>If a commit has more than one parent, the commit is a merge commit, and the second and any further
     * parents refer to the merged branch(es).</p>
     *
     * @return the list of parent commits; may be empty
     */
    @Nonnull
    List<? extends GitCommit> getParents();
}
