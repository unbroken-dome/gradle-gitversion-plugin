package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

import org.gradle.api.Action;
import org.gradle.api.logging.Logging;
import org.slf4j.Logger;
import org.unbrokendome.gradle.plugins.gitversion.core.Rule;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;


public abstract class AbstractRule<TContext extends RuleContext> implements Rule {

    private final Logger logger = Logging.getLogger(getClass());

    private final Action<TContext> action;


    protected AbstractRule(Action<TContext> action) {
        this.action = action;
    }


    @Override
    public final boolean apply(RuleEvaluationContext evaluationContext) {

        MatchResult matchResult = match(evaluationContext);
        if (matchResult.isMatch()) {
            logger.info("Rule [{}] matched because: {}", this, matchResult.getDescription());

            TContext ruleContext = createContext(evaluationContext, matchResult);
            action.execute(ruleContext);

            if (ruleContext.isSkipOtherRules()) {
                logger.info("Rule body requested to skip evaluation of other rules");
                return false;
            }

        } else {
            logger.info("Rule [{}] did not match because: {}", this, matchResult.getDescription());
        }
        return true;
    }


    @Nonnull
    protected abstract MatchResult match(RuleEvaluationContext evaluationContext);


    @Nonnull
    protected abstract TContext createContext(RuleEvaluationContext evaluationContext, MatchResult matchResult);
}
