package org.unbrokendome.gradle.plugins.gitversion.internal;

public interface MatchResult {

    boolean isMatch();

    MatchResult TRUE = () -> true;

    MatchResult FALSE = () -> false;

    static MatchResult fromBoolean(boolean match) {
        return match ? TRUE : FALSE;
    }
}
