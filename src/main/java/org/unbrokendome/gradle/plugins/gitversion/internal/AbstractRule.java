package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.Rule;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;
import org.unbrokendome.gradle.plugins.gitversion.internal.MatchResult;
import org.unbrokendome.gradle.plugins.gitversion.internal.RuleEvaluationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class AbstractRule<TContext extends RuleContext, TMatchResult extends MatchResult> implements Rule {

    private final Action<TContext> action;


    protected AbstractRule(Action<TContext> action) {
        this.action = action;
    }


    @Override
    public final boolean apply(RuleEvaluationContext evaluationContext) {
        TMatchResult matchResult = match(evaluationContext);
        if (matchResult != null && matchResult.isMatch()) {
            TContext ruleContext = createContext(evaluationContext, matchResult);
            action.execute(ruleContext);
            return !ruleContext.isSkipOtherRules();
        }
        return true;
    }


    @Nullable
    protected abstract TMatchResult match(RuleEvaluationContext evaluationContext);


    @Nonnull
    protected abstract TContext createContext(RuleEvaluationContext evaluationContext, TMatchResult matchResult);
}
