package org.unbrokendome.gradle.plugins.gitversion.core;

import org.unbrokendome.gradle.plugins.gitversion.internal.RuleEvaluationContext;


public interface Rule {

    boolean apply(RuleEvaluationContext evaluationContext);
}
