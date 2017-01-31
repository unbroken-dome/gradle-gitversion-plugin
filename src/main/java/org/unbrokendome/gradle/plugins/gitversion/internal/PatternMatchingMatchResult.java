package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.util.regex.Matcher;


public final class PatternMatchingMatchResult implements MatchResult {

    private final Matcher matcher;


    PatternMatchingMatchResult(Matcher matcher) {
        this.matcher = matcher;
    }


    @Override
    public boolean isMatch() {
        return matcher.matches();
    }


    Matcher getMatcher() {
        return matcher;
    }
}
