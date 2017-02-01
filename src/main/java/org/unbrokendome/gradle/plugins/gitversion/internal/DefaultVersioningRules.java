package org.unbrokendome.gradle.plugins.gitversion.internal;

import com.google.common.collect.ImmutableList;
import org.gradle.api.Project;
import org.gradle.api.logging.Logging;
import org.slf4j.Logger;
import org.unbrokendome.gradle.plugins.gitversion.core.Rule;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.util.List;

import javax.annotation.Nonnull;


public class DefaultVersioningRules implements VersioningRules {

    private final Logger logger = Logging.getLogger(getClass());

    private final SemVersion baseVersion;
    private final List<Rule> rules;


    DefaultVersioningRules(SemVersion baseVersion, Iterable<Rule> rules) {
        this.baseVersion = baseVersion;
        this.rules = ImmutableList.copyOf(rules);
    }


    @Override
    @Nonnull
    public SemVersion evaluate(Project project, GitRepository gitRepository) {

        logger.info("Starting rule evaluation");

        logger.info("Starting with base version: {}", baseVersion);
        MutableSemVersion version = baseVersion.cloneAsMutable();
        RuleEvaluationContext evaluationContext = new RuleEvaluationContextImpl(project, gitRepository, version);

        for (Rule rule : rules) {

            SemVersion versionBefore = version.toImmutable();

            logger.info("Applying rule: [{}]", rule);
            boolean shouldContinue = rule.apply(evaluationContext);

            if (logger.isDebugEnabled()) {
                SemVersion versionAfter = version.toImmutable();
                if (!versionBefore.equals(versionAfter)) {
                    logger.debug("Version was modified by rule; is now {}", versionAfter);
                } else {
                    logger.debug("Version was not modified by rule");
                }
            }

            if (!shouldContinue) {
                logger.info("Skipping evaluation of other rules");
                break;
            }
        }

        SemVersion finalVersion = version.toImmutable();

        logger.info("Finished rule evaluation; final version is: {}", finalVersion);

        return finalVersion;
    }
}
