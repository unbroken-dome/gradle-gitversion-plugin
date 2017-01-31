package org.unbrokendome.gradle.plugins.gitversion.internal;

import com.google.common.collect.Iterables;
import org.unbrokendome.gradle.plugins.gitversion.core.Rule;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRulesBuilder;
import org.unbrokendome.gradle.plugins.gitversion.internal.DefaultVersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;


public class DefaultVersioningRulesBuilder implements VersioningRulesBuilder {

    private static final SemVersion DEFAULT_BASE_VERSION = SemVersion.create(0, 1, 0);

    private SemVersion baseVersion = DEFAULT_BASE_VERSION;
    private List<Rule> beforeRules = new ArrayList<>(1);
    private List<Rule> rules = new ArrayList<>(5);
    private List<Rule> afterRules = new ArrayList<>(1);


    @Nonnull
    @Override
    public VersioningRulesBuilder setBaseVersion(SemVersion baseVersion) {
        this.baseVersion = SemVersion.immutableCopyOf(baseVersion);
        return this;
    }


    @Nonnull
    @Override
    public VersioningRulesBuilder addBeforeRule(Rule rule) {
        beforeRules.add(rule);
        return this;
    }


    @Nonnull
    @Override
    public VersioningRulesBuilder addRule(Rule rule) {
        rules.add(rule);
        return this;
    }


    @Nonnull
    @Override
    public VersioningRulesBuilder addAfterRule(Rule rule) {
        afterRules.add(rule);
        return this;
    }


    @Nonnull
    @Override
    public VersioningRules build() {
        Iterable<Rule> allRules = Iterables.concat(beforeRules, rules, afterRules);
        return new DefaultVersioningRules(baseVersion, allRules);
    }
}
