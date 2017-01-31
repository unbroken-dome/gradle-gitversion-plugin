package org.unbrokendome.gradle.plugins.gitversion.core;

import javax.annotation.Nonnull;


public interface PatternMatchRuleContext extends RuleContext {

    @Nonnull
    MatcherFacade getMatches();
}
