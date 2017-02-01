package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.*;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;


public class DefaultRulesContainer implements RulesContainer, RulesContainerInternal {

    private static final SemVersion DEFAULT_VERSION = SemVersion.create(0, 1, 0);

    private final VersioningRulesBuilder versioningRules = VersioningRules.builder();
    private final MutableSemVersion baseVersion = DEFAULT_VERSION.cloneAsMutable();


    @Nonnull
    @Override
    public MutableSemVersion getBaseVersion() {
        return baseVersion;
    }


    @Override
    public void always(Action<RuleContext> action) {
        Rule rule = new SimpleRule(action);
        versioningRules.addRule(rule);
    }


    @Override
    public void onBranch(String branchName, Action<RuleContext> action) {
        Rule rule = new BranchNameRule(branchName, action);
        versioningRules.addRule(rule);
    }


    @Override
    public void onBranch(Pattern branchNamePattern, Action<PatternMatchRuleContext> action) {
        Rule rule = new PatternMatchingBranchRule(branchNamePattern, action);
        versioningRules.addRule(rule);
    }


    @Override
    public void onDetachedHead(Action<RuleContext> action) {
        Rule rule = new SimplePredicateMatchingRule(action,
                context -> context.getRepository().getCurrentBranch() == null);
        versioningRules.addRule(rule);
    }


    @Override
    public void before(Action<RuleContext> action) {
        Rule rule = new SimpleRule(action);
        versioningRules.addBeforeRule(rule);
    }


    @Override
    public void after(Action<RuleContext> action) {
        Rule rule = new SimpleRule(action);
        versioningRules.addAfterRule(rule);
    }


    @Nonnull
    @Override
    public VersioningRules getVersioningRules() {
        versioningRules.setBaseVersion(baseVersion);
        return versioningRules.build();
    }
}
