package org.unbrokendome.gradle.plugins.gitversion.core;

import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;


public interface VersioningRulesBuilder {

    VersioningRulesBuilder setBaseVersion(SemVersion baseVersion);

    VersioningRulesBuilder addBeforeRule(Rule rule);

    VersioningRulesBuilder addRule(Rule rule);

    VersioningRulesBuilder addAfterRule(Rule rule);

    VersioningRules build();
}
