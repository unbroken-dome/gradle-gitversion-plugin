package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

import org.gradle.api.Action;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;


public final class DetachedHeadRule extends AbstractSimpleRule {

    private static final MatchResult MATCH = MatchResult.match("repository is in detached-head mode");
    private static final MatchResult MISMATCH = MatchResult.mismatch("repository is not in detached-head mode");


    DetachedHeadRule(Action<RuleContext> action) {
        super(action);
    }


    @Nonnull
    @Override
    protected MatchResult match(RuleEvaluationContext evaluationContext) {
        boolean detachedHead = (evaluationContext.getRepository().getCurrentBranch() == null);
        return detachedHead ? MATCH : MISMATCH;
    }


    @Override
    public String toString() {
        return "detached head";
    }
}
