package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbrokendome.gradle.plugins.gitversion.core.PatternMatchRuleContext;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;


public class PatternMatchingBranchRule extends AbstractRule<PatternMatchRuleContext, PatternMatchingMatchResult> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Pattern pattern;


    PatternMatchingBranchRule(Pattern pattern, Action<PatternMatchRuleContext> action) {
        super(action);
        this.pattern = pattern;
    }


    @Nullable
    @Override
    protected PatternMatchingMatchResult match(RuleEvaluationContext evaluationContext) {
        GitBranch currentBranch = evaluationContext.getRepository().getCurrentBranch();
        String branchName = currentBranch != null ? currentBranch.getShortName() : null;
        if (branchName != null) {
            Matcher matcher = pattern.matcher(branchName);
            if (matcher.matches()) {
                logger.info("Rule match: pattern \"{}\", matches current branch name \"{}\"",
                        pattern, branchName);
            } else {
                logger.debug("Rule skipped: pattern \"{}\"; does not match current branch name \"{}\"",
                        pattern, branchName);
            }

            return new PatternMatchingMatchResult(matcher);

        } else {
            logger.debug("Rule skipped: pattern \"{}\", current branch is not set", pattern);
            return null;
        }
    }


    @Nonnull
    @Override
    protected PatternMatchRuleContext createContext(RuleEvaluationContext evaluationContext,
                                                    PatternMatchingMatchResult matchResult) {
        DefaultMatcherFacade matches = new DefaultMatcherFacade(matchResult.getMatcher());
        return new PatternMatchRuleContextImpl(evaluationContext, matches);
    }
}
