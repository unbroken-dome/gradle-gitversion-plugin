package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import com.google.common.collect.Multimap;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitTag;
import org.unbrokendome.gradle.plugins.gitversion.util.Lazy;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOFunction;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOUtils;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JGitCommit implements GitCommit {

    private final JGitRepository repository;
    private final ObjectId objectId;
    private final Supplier<String> commitMessage = Lazy.of(this::readCommitMessage);


    JGitCommit(JGitRepository repository, ObjectId objectId) {
        this.repository = repository;
        this.objectId = objectId;
    }


    @Nonnull
    @Override
    public String getId() {
        return objectId.name();
    }


    @Nonnull
    @Override
    public String getMessage() {
        return commitMessage.get();
    }


    private String readCommitMessage() {
        return withRevCommit(RevCommit::getFullMessage);
    }


    private <T> T withRevCommit(IOFunction<RevCommit, T> function) {
        return IOUtils.unchecked(() -> {
            try (RevWalk revWalk = new RevWalk(repository.getRepository())) {
                RevCommit commit = revWalk.parseCommit(objectId);
                return function.apply(commit);
            }
        });
    }


    @Nonnull
    @Override
    public Collection<? extends GitTag> getTags() {
        Multimap<ObjectId, JGitTag> tagsByCommitId = repository.getTagsByCommitId();
        return tagsByCommitId.get(objectId);
    }


    @Nonnull
    @Override
    public List<? extends GitCommit> getParents() {
        return withRevCommit(revCommit ->
                streamParents(revCommit)
                        .map(parentRevCommit -> new JGitCommit(repository, parentRevCommit))
                        .collect(Collectors.toList()));
    }


    @Nonnull
    private Stream<RevCommit> streamParents(RevCommit revCommit) {
        return revCommit.getParents() != null ? Arrays.stream(revCommit.getParents()) : Stream.empty();
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof JGitCommit && equals((JGitCommit) obj);
    }


    private boolean equals(JGitCommit other) {
        return repository == other.repository
                && objectId.equals(other.objectId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(repository, objectId);
    }
}
