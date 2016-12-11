package org.unbrokendome.gradle.plugins.gitversion

import java.util.regex.Matcher


interface MatcherFacade {
    int size()

    String getAt(int index)

    String getAt(String groupName)
}


class DefaultMatcherFacade implements MatcherFacade {

    private final Matcher matcher

    DefaultMatcherFacade(Matcher matcher) {
        this.matcher = matcher
    }

    int size() { matcher.groupCount() + 1 }

    String getAt(int index) { matcher.group(index) }

    String getAt(String groupName) { matcher.group(groupName) }
}


class NullMatcherFacade implements MatcherFacade {

    static final NullMatcherFacade INSTANCE = new NullMatcherFacade()

    int size() { 0 }

    String getAt(int index) {
        throw new IndexOutOfBoundsException("Matcher has no group with index $index")
    }

    String getAt(String groupName) {
        throw new IndexOutOfBoundsException("Matcher has no group with name \"$groupName\"")
    }
}
