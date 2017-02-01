package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.text.MessageFormat;
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


    @Nonnull
    @Override
    public String getDescription() {
        if (matcher.matches()) {
            return MessageFormat.format("current branch name \"{0}\" matches pattern",
                    matcher.group());
        } else {
            String branchName = matcher.replaceFirst("");
            return MessageFormat.format("current branch name \"{0}\" does not match pattern",
                    branchName);
        }
    }
}
