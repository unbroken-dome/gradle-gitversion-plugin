package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;


public class BranchNameRule extends AbstractSimpleRule {

    private final String branchName;


    BranchNameRule(String branchName, Action<RuleContext> action) {
        super(action);
        this.branchName = branchName;
    }


    @Nonnull
    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {

        GitBranch currentBranch = evaluationContext.getRepository().getCurrentBranch();

        String matchDescription;
        if (currentBranch != null) {
            matchDescription = "current branch name is \"" + currentBranch.getShortName() + "\"";
        } else {
            matchDescription = "current branch is not set";
        }

        if (currentBranch != null && branchName.equals(currentBranch.getShortName())) {
            return MatchResult.match(matchDescription);
        } else {
            return MatchResult.mismatch(matchDescription);
        }
    }


    @Override
    public String toString() {
        return "branch name \"" + branchName + "\"";
    }
}
