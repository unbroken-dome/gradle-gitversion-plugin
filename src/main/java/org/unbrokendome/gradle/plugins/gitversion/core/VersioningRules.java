package org.unbrokendome.gradle.plugins.gitversion.core;

import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.internal.DefaultVersioningRulesBuilder;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;


public interface VersioningRules {

    SemVersion evaluate(Project project, GitRepository gitRepository);

    static VersioningRulesBuilder builder() {
        return new DefaultVersioningRulesBuilder();
    }
}
