package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOUtils;

import javax.annotation.Nonnull;
import java.util.Objects;


public class JGitBranch implements GitBranch {

    private final JGitRepository repository;
    private final Ref ref;


    JGitBranch(JGitRepository repository, Ref ref) {
        this.repository = repository;
        this.ref = ref;
    }


    @Nonnull
    @Override
    public String getShortName() {
        String fullName = getFullName();
        if (fullName.startsWith(Constants.R_HEADS)) {
            return fullName.substring(Constants.R_HEADS.length());
        } else if (fullName.startsWith(Constants.R_REMOTES)) {
            return fullName.substring(Constants.R_REMOTES.length());
        } else {
            return fullName;
        }
    }


    @Nonnull
    @Override
    public String getFullName() {
        return ref.getName();
    }


    @Nonnull
    @Override
    public GitCommit getHead() {
        ObjectId objectId = IOUtils.unchecked(() -> ref.getLeaf().getObjectId());
        return new JGitCommit(repository, objectId);
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof JGitBranch && equals((JGitBranch) obj);
    }


    private boolean equals(JGitBranch other) {
        return repository == other.repository
                && ref.equals(other.ref);
    }


    @Override
    public int hashCode() {
        return Objects.hash(repository, ref);
    }
}
