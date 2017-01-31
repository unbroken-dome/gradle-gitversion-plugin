package org.unbrokendome.gradle.plugins.gitversion.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;

public class MockGitCommit implements GitCommit {

    private static final Random random = new Random();

    private final List<GitCommit> parents;
    private final String message;
    private final String id;
    private final List<MockGitTag> tags = new ArrayList<>();

    public MockGitCommit(Iterable<GitCommit> parents, String message) {
        this.parents = ImmutableList.copyOf(parents);
        this.message = message;
        this.id = Hashing.sha1().newHasher()
                .putLong(random.nextLong())
                .hash()
                .toString();
    }

    @Nonnull
    public List<GitCommit> getParents() {
        return parents;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    @Nonnull
    @Override
    public String getMessage() {
        return message;
    }

    @Nonnull
    @Override
    public Collection<? extends GitTag> getTags() {
        return ImmutableList.copyOf(tags);
    }

    public void addTag(MockGitTag tag) {
        tags.add(tag);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MockGitCommit && id.equals(((MockGitCommit) obj).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
