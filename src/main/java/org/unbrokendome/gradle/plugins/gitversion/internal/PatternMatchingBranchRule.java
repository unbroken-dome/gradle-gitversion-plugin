package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.PatternMatchRuleContext;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;


public final class PatternMatchingBranchRule extends AbstractRule<PatternMatchRuleContext> {

    private final Pattern pattern;


    PatternMatchingBranchRule(Pattern pattern, Action<PatternMatchRuleContext> action) {
        super(action);
        this.pattern = pattern;
    }


    @Nonnull
    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {
        GitBranch currentBranch = evaluationContext.getRepository().getCurrentBranch();
        String branchName = currentBranch != null ? currentBranch.getShortName() : null;
        if (branchName != null) {
            Matcher matcher = pattern.matcher(branchName);
            return new PatternMatchingMatchResult(matcher);

        } else {
            return MatchResult.mismatch("current branch is not set");
        }
    }


    @Nonnull
    @Override
    protected PatternMatchRuleContext createContext(RuleEvaluationContext evaluationContext,
                                                    MatchResult matchResult) {
        PatternMatchingMatchResult patternMatchResult = (PatternMatchingMatchResult) matchResult;
        DefaultMatcherFacade matches = new DefaultMatcherFacade(patternMatchResult.getMatcher());
        return new PatternMatchRuleContextImpl(evaluationContext, matches);
    }


    @Override
    public String toString() {
        return "branch name pattern \"" + pattern + "\"";
    }
}
