package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitTag;

import java.util.Objects;

import javax.annotation.Nonnull;


public class JGitTag implements GitTag {

    private final JGitRepository repository;
    private final String name;
    private final Ref ref;


    JGitTag(JGitRepository repository, String name, Ref ref) {
        this.repository = repository;
        this.name = name;
        this.ref = ref;
    }


    @Nonnull
    @Override
    public String getName() {
        return name;
    }


    @Nonnull
    @Override
    public GitCommit getTarget() {
        ObjectId targetObjectId = getTargetObjectId();
        return new JGitCommit(repository, targetObjectId);
    }


    ObjectId getTargetObjectId() {
        Ref peeledRef = repository.getRepository().peel(ref);
        if (peeledRef.getPeeledObjectId() != null) {
            return peeledRef.getPeeledObjectId();
        } else {
            return ref.getObjectId();
        }
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof JGitTag && equals((JGitTag) obj);
    }


    private boolean equals(JGitTag other) {
        return repository == other.repository
                && name.equals(other.name)
                && ref.equals(other.ref);
    }


    @Override
    public int hashCode() {
        return Objects.hash(repository, name, ref);
    }
}
