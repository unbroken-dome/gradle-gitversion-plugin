package org.unbrokendome.gradle.plugins.gitversion

class CollectionUtils {

    /**
     * Finds the first item on an iterator that also appears on any of a number of other
     * iterators.
     *
     * <p>This could have easily been implemented by Set logic, but the iterators may represent
     * possibly quite long sequences, so we don't want to consume more items than necessary.
     *
     * <p>The iterators that are passed in will be (partly) consumed by this method, so the
     * caller should not make any assumptions about their state when the method returns.
     *
     * @param refIterator the iterator with the reference items to look for
     * @param otherIterators a map containing a number of other iterators as values. The map keys
     *        may be used to identify from which iterator the matching item came.
     * @return a {@link Tuple2} that consists of: <ol>
     *         <li>the key of the iterator in {@code otherIterators} that contained the match;</li>
     *         <li>the common item.</li>
     *         </ol>
     *         Returns {@code null} if {@code refIterator} is disjoint with all {@code otherIterators}.
     */
    static <K, T> Tuple2<K, T> findFirstCommonItem(
            Iterator<T> refIterator,
            Map<K, Iterator<T>> otherIterators) {

        Set<T> refItemsVisited = []
        Map<K, Set<T>> otherItemsVisited = [:]
        otherIterators.collectEntries(otherItemsVisited) { [ it.key, new HashSet() ] }

        for (T item : refIterator) {
            def found = otherItemsVisited.find { key, value -> value.contains(item) }
            if (found) {
                return new Tuple2<>(found.key, item)
            }
            refItemsVisited << item

            def nonExhausted = otherIterators.findAll { key, value -> value.hasNext() }
            if (nonExhausted.isEmpty()) {
                break
            }
            for (def other : nonExhausted) {
                def otherItem = ++other.value
                if (refItemsVisited.contains(otherItem)) {
                    return new Tuple2<>(other.key, otherItem)
                }
                otherItemsVisited[other.key] << otherItem
            }
        }

        if (refIterator.hasNext()) {
            for (T item : refIterator) {
                def found = otherItemsVisited.find { key, value -> value.contains(item) }
                if (found) {
                    return new Tuple2<>(found.key, item)
                }
            }

        } else {
            for (;;) {
                def nonExhausted = otherIterators.findAll { it.value.hasNext() }
                if (nonExhausted.isEmpty()) {
                    break
                }
                def found = nonExhausted.collectEntries { key, value -> [ key, value.next() ]}
                        .find { refItemsVisited.contains(it.value) }
                if (found) {
                    return new Tuple2<>(found.key, found.value)
                }
            }
        }

        null
    }

    /**
     * Finds the first item in a collection that also appears in any of a number of other collections.
     *
     * @param ref the collection containing the reference items
     * @param others a {@link Map} containing a number of other collections as values. The map keys
     *        may be used to identify from which collection the matching item came
     *
     * @return a {@link Tuple2} that consists of: <ol>
     *         <li>the key of the iterator in {@code otherIterators} that contained the match;</li>
     *         <li>the common item.</li>
     *         </ol>
     *         Returns {@code null} if {@code ref} is disjoint with all {@code others}.
     */
    static <K, T> Tuple2<K, T> findFirstCommonItem(Iterable<T> ref, Map<K, ? extends Iterable<T>> others) {
        findFirstCommonItem(ref.iterator(),
                others.collectEntries { key, value -> [ key, value.iterator() ] } as Map<K, Iterator<T>>)
    }

    /**
     * Finds the first item in an iterator that also appears in another iterator.
     *
     * @param ref the iterator returning the reference items
     * @param others the iterator to compare with
     *
     * @return the first common item, or {@code null} if the two iterators are disjoint
     */
    static <T> T findFirstCommonItem(Iterator<T> refIterator, Iterator<T> otherIterator) {
        findFirstCommonItem(refIterator, Collections.singletonMap(0, otherIterator)) ?.second
    }

    /**
     * Finds the first item in a collection that also appears in another collection.
     *
     * @param ref the collection containing the reference items
     * @param others the collection to compare with
     *
     * @return the first common item, or {@code null} if the two collections are disjoint
     */
    static <T> T findFirstCommonItem(Iterable<T> ref, Iterable<T> other) {
        findFirstCommonItem(ref.iterator(), other.iterator())
    }
}
