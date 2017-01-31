package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.unbrokendome.gradle.plugins.gitversion.core.MatcherFacade;
import org.unbrokendome.gradle.plugins.gitversion.core.PatternMatchRuleContext;

import javax.annotation.Nonnull;


public class PatternMatchRuleContextImpl extends AbstractRuleContext implements PatternMatchRuleContext {

    private final MatcherFacade matches;


    PatternMatchRuleContextImpl(RuleEvaluationContext evaluationContext, MatcherFacade matches) {
        super(evaluationContext);
        this.matches = matches;
    }


    @Nonnull
    @Override
    public MatcherFacade getMatches() {
        return matches;
    }
}
