package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

public interface MatchResult {

    boolean isMatch();

    /**
     * Gets a description as to why the match succeeded or failed; intended for logging purposes.
     *
     * @return a description of the (mis)match
     */
    @Nonnull
    String getDescription();

    @Nonnull
    static MatchResult fromBoolean(boolean match, String description) {
        return new SimpleMatchResult(match, description);
    }


    @Nonnull
    static MatchResult match(String description) {
        return fromBoolean(true, description);
    }


    @Nonnull
    static MatchResult mismatch(String description) {
        return fromBoolean(false, description);
    }
}
