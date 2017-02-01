package org.unbrokendome.gradle.plugins.gitversion.internal;

import javax.annotation.Nonnull;


public final class SimpleMatchResult implements MatchResult {

    private final boolean match;
    private final String description;


    SimpleMatchResult(boolean match, String description) {
        this.match = match;
        this.description = description;
    }


    @Override
    public boolean isMatch() {
        return match;
    }


    @Override
    @Nonnull
    public String getDescription() {
        return description;
    }
}
