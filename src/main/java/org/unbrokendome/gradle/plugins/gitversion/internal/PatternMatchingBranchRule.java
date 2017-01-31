package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.PatternMatchRuleContext;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternMatchingBranchRule extends AbstractRule<PatternMatchRuleContext, PatternMatchingMatchResult> {

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
            return new PatternMatchingMatchResult(matcher);
        }
        return null;
    }


    @Nonnull
    @Override
    protected PatternMatchRuleContext createContext(RuleEvaluationContext evaluationContext,
                                                    PatternMatchingMatchResult matchResult) {
        DefaultMatcherFacade matches = new DefaultMatcherFacade(matchResult.getMatcher());
        return new PatternMatchRuleContextImpl(evaluationContext, matches);
    }
}
