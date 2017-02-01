package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.*;

import javax.annotation.Nonnull;


public final class AlwaysRule extends AbstractSimpleRule {

    private static final MatchResult ALWAYS_MATCH = new SimpleMatchResult(true,
            "this rule always matches");


    AlwaysRule(Action<RuleContext> action) {
        super(action);
    }


    @Nonnull
    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {
        return ALWAYS_MATCH;
    }


    @Nonnull
    @Override
    protected SimpleRuleContext createContext(RuleEvaluationContext evaluationContext, MatchResult matchResult) {
        return new SimpleRuleContext(evaluationContext);
    }


    @Override
    public String toString() {
        return "always";
    }
}
