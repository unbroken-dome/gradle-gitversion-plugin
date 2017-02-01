package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbrokendome.gradle.plugins.gitversion.core.Rule;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;


public abstract class AbstractRule<TContext extends RuleContext, TMatchResult extends MatchResult> implements Rule {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

            if (ruleContext.isSkipOtherRules()) {
                logger.debug("Rule body requested to skip evaluation of other rules");
                return false;
            }
        }
        return true;
    }


    @Nullable
    protected abstract TMatchResult match(RuleEvaluationContext evaluationContext);


    @Nonnull
    protected abstract TContext createContext(RuleEvaluationContext evaluationContext, TMatchResult matchResult);
}
