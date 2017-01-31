package org.unbrokendome.gradle.plugins.gitversion.internal;

import com.google.common.collect.ImmutableList;
import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.core.Rule;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.util.List;

import javax.annotation.Nonnull;


public class DefaultVersioningRules implements VersioningRules {

    private final SemVersion baseVersion;
    private final List<Rule> rules;


    DefaultVersioningRules(SemVersion baseVersion, Iterable<Rule> rules) {
        this.baseVersion = baseVersion;
        this.rules = ImmutableList.copyOf(rules);
    }


    @Override
    @Nonnull
    public SemVersion evaluate(Project project, GitRepository gitRepository) {

        MutableSemVersion version = baseVersion.cloneAsMutable();
        RuleEvaluationContext evaluationContext = new RuleEvaluationContextImpl(project, gitRepository, version);

        for (Rule rule : rules) {
            boolean shouldContinue = rule.apply(evaluationContext);
            if (!shouldContinue) {
                break;
            }
        }

        return version.toImmutable();
    }
}
