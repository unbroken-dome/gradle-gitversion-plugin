package org.unbrokendome.gradle.plugins.gitversion.core;

import javax.annotation.Nonnull;

import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;


@SuppressWarnings("UnusedReturnValue")
public interface VersioningRulesBuilder {

    @Nonnull
    VersioningRulesBuilder setBaseVersion(SemVersion baseVersion);

    @Nonnull
    VersioningRulesBuilder addBeforeRule(Rule rule);

    @Nonnull
    VersioningRulesBuilder addRule(Rule rule);

    @Nonnull
    VersioningRulesBuilder addAfterRule(Rule rule);

    @Nonnull
    VersioningRules build();
}
