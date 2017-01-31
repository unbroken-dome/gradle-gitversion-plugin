package org.unbrokendome.gradle.plugins.gitversion.core;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;


public interface MatcherFacade extends Iterable<String> {

    int size();

    String getAt(int groupIndex);

    String getAt(String groupName);


    default List<String> toList() {
        int size = size();
        ImmutableList.Builder<String> items = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            items.add(getAt(i));
        }
        return items.build();
    }


    @Override
    default Iterator<String> iterator() {
        return toList().iterator();
    }
}
