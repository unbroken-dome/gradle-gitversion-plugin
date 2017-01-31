package org.unbrokendome.gradle.plugins.gitversion.internal;

import org.unbrokendome.gradle.plugins.gitversion.core.MatcherFacade;


class NullMatcherFacade implements MatcherFacade {

    public static final NullMatcherFacade INSTANCE = new NullMatcherFacade();


    private NullMatcherFacade() {
    }


    @Override
    public int size() {
        return 0;
    }


    @Override
    public String getAt(int groupIndex) {
        throw new IndexOutOfBoundsException("Matcher has no group with index " + groupIndex);
    }


    @Override
    public String getAt(String groupName) {
        throw new IndexOutOfBoundsException("Matcher has no group with name \"" + groupName + "\"");
    }
}
