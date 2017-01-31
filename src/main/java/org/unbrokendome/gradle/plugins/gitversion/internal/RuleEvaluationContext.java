package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;

import javax.annotation.Nonnull;


public interface RuleEvaluationContext {

    @Nonnull
    Project getProject();

    @Nonnull
    GitRepository getRepository();

    @Nonnull
    MutableSemVersion getVersion();
}
