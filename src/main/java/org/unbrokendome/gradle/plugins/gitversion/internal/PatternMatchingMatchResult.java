package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.util.regex.Matcher;

import javax.annotation.Nonnull;


public final class PatternMatchingMatchResult implements MatchResult {

    private final Matcher matcher;


    PatternMatchingMatchResult(Matcher matcher) {
        this.matcher = matcher;
    }


    @Override
    public boolean isMatch() {
        return matcher.matches();
    }


    @Nonnull
    Matcher getMatcher() {
        return matcher;
    }
}
