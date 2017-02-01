package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;


public abstract class AbstractSimpleRule extends AbstractRule<RuleContext> {

    protected AbstractSimpleRule(Action<RuleContext> action) {
        super(action);
    }

    @Nonnull
    @Override
    protected RuleContext createContext(RuleEvaluationContext evaluationContext, MatchResult matchResult) {
        return new SimpleRuleContext(evaluationContext);
    }
}
