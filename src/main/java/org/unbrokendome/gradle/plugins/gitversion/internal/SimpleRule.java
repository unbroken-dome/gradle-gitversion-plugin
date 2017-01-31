package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.*;

import javax.annotation.Nonnull;


public class SimpleRule extends AbstractRule<RuleContext, MatchResult> {

    SimpleRule(Action<RuleContext> action) {
        super(action);
    }


    @Nonnull
    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {
        return MatchResult.TRUE;
    }


    @Nonnull
    @Override
    protected SimpleRuleContext createContext(RuleEvaluationContext evaluationContext, MatchResult matchResult) {
        return new SimpleRuleContext(evaluationContext);
    }
}
