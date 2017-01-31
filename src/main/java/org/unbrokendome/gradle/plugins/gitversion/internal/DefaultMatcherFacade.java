package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.unbrokendome.gradle.plugins.gitversion.core.MatcherFacade;

import java.util.regex.Matcher;

import javax.annotation.Nonnull;


public final class DefaultMatcherFacade implements MatcherFacade {

    private final Matcher matcher;


    DefaultMatcherFacade(Matcher matcher) {
        this.matcher = matcher;
    }


    @Override
    public int size() {
        return matcher.groupCount() + 1;
    }


    @Nonnull
    @Override
    public String getAt(int groupIndex) {
        return matcher.group(groupIndex);
    }


    @Nonnull
    @Override
    public String getAt(String groupName) {
        return matcher.group(groupName);
    }
}
