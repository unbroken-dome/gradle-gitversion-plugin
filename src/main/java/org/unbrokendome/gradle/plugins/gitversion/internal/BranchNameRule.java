package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.gradle.api.logging.Logging;
import org.slf4j.Logger;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;


public class BranchNameRule extends AbstractRule<RuleContext, MatchResult> {

    private final Logger logger = Logging.getLogger(getClass());

    private final String branchName;


    BranchNameRule(String branchName, Action<RuleContext> action) {
        super(action);
        this.branchName = branchName;
    }


    @Nullable
    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {

        logger.debug("Determining if branch rule \"{}\" is a match...", branchName);

        GitBranch currentBranch = evaluationContext.getRepository().getCurrentBranch();
        if (currentBranch == null) {
            logger.debug("Rule skipped: \"{}\": current branch is not set", branchName);
            return MatchResult.FALSE;

        } else if (branchName.equals(currentBranch.getShortName())) {
            logger.info("Rule match: current branch is \"{}\"", currentBranch.getShortName());
            return MatchResult.TRUE;

        } else {
            logger.debug("Rule skipped: \"{}\"; current branch is \"{}\"",
                    branchName, currentBranch.getShortName());
            return MatchResult.FALSE;
        }
    }


    @Nonnull
    @Override
    protected RuleContext createContext(RuleEvaluationContext evaluationContext, MatchResult matchResult) {
        return new SimpleRuleContext(evaluationContext);
    }
}
