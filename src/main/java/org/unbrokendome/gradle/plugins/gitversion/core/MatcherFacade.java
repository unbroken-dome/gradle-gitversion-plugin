package org.unbrokendome.gradle.plugins.gitversion.core;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;


/**
 * Facade object for the result of a regular expression pattern match, for more convenient
 * use in Groovy code.
 */
public interface MatcherFacade extends Iterable<String> {

    /**
     * Gets the number of matches.
     * @return the number of matches
     */
    int size();

    /**
     * Gets the input subsequence captured by the given group during the match operation.
     *
     * Capturing groups start at index one. The match at index 0 contains the entire input sequence
     * that was matched.
     *
     * @param groupIndex the index of the capturing group
     * @return the input subsequence captured by the given group
     * @see java.util.regex.Matcher#group(int)
     */
    @Nonnull
    String getAt(int groupIndex);

    /**
     * Gets the input subsequence captured by the given named group during the match operation.
     *
     * @param groupName the name of a capturing group
     * @return the input subsequence of the given group
     * @see java.util.regex.Matcher#group(String)
     */
    @Nonnull
    String getAt(String groupName);

    /**
     * Returns a list containing all capturing results.
     *
     * @return a list containing the group capture results, as if returned by {@link #getAt(int)}
     */
    @Nonnull
    default List<String> toList() {
        int size = size();
        ImmutableList.Builder<String> items = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            items.add(getAt(i));
        }
        return items.build();
    }


    @Override
    @Nonnull
    default Iterator<String> iterator() {
        return toList().iterator();
    }
}
