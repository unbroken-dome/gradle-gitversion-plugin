package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

import org.unbrokendome.gradle.plugins.gitversion.core.MatcherFacade;
import org.unbrokendome.gradle.plugins.gitversion.core.TaggedCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitTag;


public class TaggedCommitImpl implements TaggedCommit {

    private final GitTag tag;
    private final MatcherFacade matches;


    public TaggedCommitImpl(GitTag tag, MatcherFacade matches) {
        this.tag = tag;
        this.matches = matches;
    }


    @Nonnull
    @Override
    public GitTag getTag() {
        return tag;
    }


    @Nonnull
    @Override
    public MatcherFacade getMatches() {
        return matches;
    }
}
