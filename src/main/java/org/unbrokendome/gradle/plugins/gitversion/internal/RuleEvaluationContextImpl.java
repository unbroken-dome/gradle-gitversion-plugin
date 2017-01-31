package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;

import javax.annotation.Nonnull;


class RuleEvaluationContextImpl implements RuleEvaluationContext {

    private final Project project;
    private final GitRepository repository;
    private final MutableSemVersion version;


    RuleEvaluationContextImpl(Project project, GitRepository repository, MutableSemVersion version) {
        this.project = project;
        this.repository = repository;
        this.version = version;
    }


    @Nonnull
    @Override
    public Project getProject() {
        return project;
    }


    @Nonnull
    @Override
    public GitRepository getRepository() {
        return repository;
    }


    @Nonnull
    @Override
    public MutableSemVersion getVersion() {
        return version;
    }
}
