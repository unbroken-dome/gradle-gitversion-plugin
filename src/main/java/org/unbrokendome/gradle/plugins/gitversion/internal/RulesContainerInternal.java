package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

import org.unbrokendome.gradle.plugins.gitversion.core.RulesContainer;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;


public interface RulesContainerInternal extends RulesContainer {

    @Nonnull
    VersioningRules getVersioningRules();
}
