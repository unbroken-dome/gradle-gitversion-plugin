package org.unbrokendome.gradle.plugins.gitversion.core;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.gradle.api.Action;
import org.gradle.util.ConfigureUtil;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;


public interface RulesContainer {

    @Nonnull
    MutableSemVersion getBaseVersion();


    default void setBaseVersion(String versionString) {
        SemVersion version = SemVersion.parse(versionString);
        getBaseVersion().setFrom(version);
    }


    /**
     * Adds a rule that will always be evaluated. It is equivalent to a branch rule whose condition always
     * matches.
     *
     * The rule will be evaluated after any {@code before} rule, and in order of definition regarding other
     * {@code always} or branch rules.
     *
     * @param action an action to configure the rule
     */
    void always(Action<RuleContext> action);


    /**
     * Adds a rule that will always be evaluated. It is equivalent to a branch rule whose condition always
     * matches.
     *
     * The rule will be evaluated after any {@code before} rule, and in order of definition regarding other
     * {@code always} or branch rules.
     *
     * @param closure a closure to configure the rule
     */
    default void always(@DelegatesTo(RuleContext.class) Closure closure) {
        Action<RuleContext> action = ConfigureUtil.configureUsing(closure);
        always(action);
    }


    void onBranch(String branchName, Action<RuleContext> action);


    void onBranch(Pattern branchNamePattern, Action<PatternMatchRuleContext> action);



    default void onBranch(Pattern branchNamePattern, @DelegatesTo(PatternMatchRuleContext.class) Closure closure) {
        Action<PatternMatchRuleContext> action = ConfigureUtil.configureUsing(closure);
        onBranch(branchNamePattern, action);
    }


    default void onBranch(String branchName, @DelegatesTo(RuleContext.class) Closure closure) {
        Action<RuleContext> action = ConfigureUtil.configureUsing(closure);
        onBranch(branchName, action);
    }


    void onDetachedHead(Action<RuleContext> action);


    default void onDetachedHead(@DelegatesTo(RuleContext.class) Closure closure) {
        Action<RuleContext> action = ConfigureUtil.configureUsing(closure);
        onDetachedHead(action);
    }


    void before(Action<RuleContext> action);


    default void before(@DelegatesTo(RuleContext.class) Closure closure) {
        Action<RuleContext> action = ConfigureUtil.configureUsing(closure);
        before(action);
    }


    void after(Action<RuleContext> action);


    default void after(@DelegatesTo(RuleContext.class) Closure closure) {
        Action<RuleContext> action = ConfigureUtil.configureUsing(closure);
        after(action);
    }
}
