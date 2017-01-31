package org.unbrokendome.gradle.plugins.gitversion.core;

import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitTag;
import org.unbrokendome.gradle.plugins.gitversion.model.HasObjectId;

import javax.annotation.Nonnull;


public interface TaggedCommit extends HasObjectId {

    GitTag getTag();

    default String getTagName() {
        return getTag().getName();
    }

    default GitCommit getCommit() {
        return getTag().getTarget();
    }

    @Nonnull
    @Override
    default String getId() {
        return getCommit().getId();
    }

    MatcherFacade getMatches();
}
