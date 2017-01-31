package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;

import javax.annotation.Nonnull;
import java.util.function.Predicate;


public abstract class AbstractPredicateMatchingRule<TContext extends RuleContext> extends AbstractRule<TContext, MatchResult> {

    private final Predicate<RuleEvaluationContext> predicate;


    AbstractPredicateMatchingRule(Action<TContext> action, Predicate<RuleEvaluationContext> predicate) {
        super(action);
        this.predicate = predicate;
    }


    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {
        boolean match = predicate.test(evaluationContext);
        return MatchResult.fromBoolean(match);
    }


    @Nonnull
    @Override
    protected TContext createContext(RuleEvaluationContext evaluationContext, MatchResult matchResult) {
        return createContext(evaluationContext);
    }


    protected abstract TContext createContext(RuleEvaluationContext evaluationContext);
}
