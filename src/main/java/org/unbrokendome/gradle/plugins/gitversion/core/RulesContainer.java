package org.unbrokendome.gradle.plugins.gitversion.core;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.gradle.api.Action;
import org.gradle.util.ConfigureUtil;
import org.unbrokendome.gradle.plugins.gitversion.version.MutableSemVersion;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.util.regex.Pattern;


public interface RulesContainer {


    MutableSemVersion getBaseVersion();


    default void setBaseVersion(String versionString) {
        SemVersion version = SemVersion.parse(versionString);
        getBaseVersion().setFrom(version);
    }


    void always(Action<RuleContext> action);


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
