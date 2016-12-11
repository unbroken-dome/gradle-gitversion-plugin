package org.unbrokendome.gradle.plugins.gitversion

import java.util.regex.Pattern

/**
 * Represents a rule for determining the version based on the current branch.
 */
class BranchRule {
    /**
     * A regular expression pattern that the branch name must match for the rule to apply.
     */
    final Pattern branchNamePattern

    /**
     * A closure that should be executed if the branch name matches the pattern.
     */
    final Closure<?> configClosure

    BranchRule(Pattern branchNamePattern,
               Closure<?> configClosure) {
        this.branchNamePattern = branchNamePattern
        this.configClosure = configClosure
    }
}
