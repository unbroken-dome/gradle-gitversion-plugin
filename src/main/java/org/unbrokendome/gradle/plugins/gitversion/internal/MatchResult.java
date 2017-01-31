package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;

public interface MatchResult {

    boolean isMatch();

    MatchResult TRUE = () -> true;

    MatchResult FALSE = () -> false;

    @Nonnull
    static MatchResult fromBoolean(boolean match) {
        return match ? TRUE : FALSE;
    }
}
