package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.core.RuleContext;
import org.unbrokendome.gradle.plugins.gitversion.internal.RuleEvaluationContext;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;

import javax.annotation.Nonnull;


public abstract class AbstractRuleContext implements RuleContext {

    private final RuleEvaluationContext evaluationContext;
    private boolean skipOtherRules = false;


    protected AbstractRuleContext(RuleEvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }


    @Override
    @Nonnull
    public MutableSemVersion getVersion() {
        return evaluationContext.getVersion();
    }


    @Nonnull
    @Override
    public Project getProject() {
        return evaluationContext.getProject();
    }


    @Nonnull
    @Override
    public GitRepository getRepository() {
        return evaluationContext.getRepository();
    }


    @Override
    public boolean isSkipOtherRules() {
        return skipOtherRules;
    }


    @Override
    public void setSkipOtherRules(boolean skipOtherRules) {
        this.skipOtherRules = skipOtherRules;
    }
}
