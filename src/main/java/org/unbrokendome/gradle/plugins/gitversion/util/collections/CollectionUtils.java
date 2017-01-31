package org.unbrokendome.gradle.plugins.gitversion.util.collections;

import groovy.lang.Tuple2;

import java.util.*;
import java.util.stream.Collectors;


public final class CollectionUtils {

    private CollectionUtils() { }


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
     * @param <K> the key type
     * @param <T> the item type
     * @param refIterator the iterator with the reference items to look for
     * @param otherIterators a map containing a number of other iterators as values. The map keys
     *        may be used to identify from which iterator the matching item came.
     * @return a {@link Tuple2} that consists of: <ol>
     *         <li>the key of the iterator in {@code otherIterators} that contained the match;</li>
     *         <li>the common item.</li>
     *         </ol>
     *         Returns {@code null} if {@code refIterator} is disjoint with all {@code otherIterators}.
     */
    public static <K, T> Tuple2<K, T> findFirstCommonItem(
            Iterator<T> refIterator,
            Map<K, ? extends Iterator<T>> otherIterators) {

        return new FirstCommonItemFinder<>(refIterator, otherIterators).find();
    }


    /**
     * Finds the first item in a collection that also appears in any of a number of other collections.
     *
     * @param <K> the key type
     * @param <T> the item type
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
    public static <K, T> Tuple2<K, T> findFirstCommonItem(Iterable<T> ref, Map<K, ? extends Iterable<T>> others) {

        Map<K, Iterator<T>> otherIterators = org.gradle.util.CollectionUtils.collectMapValues(others.keySet(),
                k -> others.get(k).iterator());
        return findFirstCommonItem(ref.iterator(), otherIterators);
    }

    /**
     * Finds the first item in an iterator that also appears in another iterator.
     *
     * @param <T> the item type
     * @param refIterator the iterator returning the reference items
     * @param otherIterator the iterator to compare with
     *
     * @return the first common item, or {@code null} if the two iterators are disjoint
     */
    public static <T> T findFirstCommonItem(Iterator<T> refIterator, Iterator<T> otherIterator) {
        Tuple2<Integer, T> result = findFirstCommonItem(refIterator, Collections.singletonMap(0, otherIterator));
        return result != null ? result.getSecond() : null;
    }

    /**
     * Finds the first item in a collection that also appears in another collection.
     *
     * @param <T> the item type
     * @param ref the collection containing the reference items
     * @param other the collection to compare with
     *
     * @return the first common item, or {@code null} if the two collections are disjoint
     */
    public static <T> T findFirstCommonItem(Iterable<T> ref, Iterable<T> other) {
        return findFirstCommonItem(ref.iterator(), other.iterator());
    }


    private static class FirstCommonItemFinder<K, T> {

        private final Iterator<T> refIterator;
        private final Map<K, ? extends Iterator<T>> otherIterators;

        private final Map<T, Integer> refItemsVisited = new HashMap<>();

        private final Map<K, Set<T>> otherItemsVisited;
        private Tuple2<K, T> result;


        FirstCommonItemFinder(Iterator<T> refIterator, Map<K, ? extends Iterator<T>> otherIterators) {
            this.refIterator = refIterator;
            this.otherIterators = otherIterators;

            otherItemsVisited = new HashMap<>();
            for (K key : otherIterators.keySet()) {
                otherItemsVisited.put(key, new HashSet<>());
            }
        }


        Tuple2<K, T> find() {

            int index = 0;
            while (refIterator.hasNext()) {
                T item = refIterator.next();
                if (findInVisitedOthers(item)) {
                    break;
                }
                refItemsVisited.put(item, index++);
                if (hasResult()) {
                    break;
                }
                if (advanceOthers() && hasResult()) {
                    break;
                }
            }

            //noinspection StatementWithEmptyBody
            while (!hasResult() && advanceOthers()) { }

            return result;
        }


        private boolean hasResult() {
            return result != null;
        }


        private void setResult(K key, T value) {
            result = new Tuple2<>(key, value);
        }


        private boolean findInVisitedOthers(T item) {
            Optional<K> found = otherItemsVisited.entrySet().stream()
                    .filter(e -> e.getValue().contains(item))
                    .map(Map.Entry::getKey)
                    .findFirst();
            if (found.isPresent()) {
                setResult(found.get(), item);
                return true;
            } else {
                return false;
            }
        }


        private boolean advanceOthers() {
            Map<K, Iterator<T>> nonExhausted = findNonExhaustedOthers();

            K foundOtherKey = null;
            T foundRefItem = null;
            int foundRefItemIndex = -1;

            for (Map.Entry<K, Iterator<T>> entry : nonExhausted.entrySet()) {
                T otherItem = entry.getValue().next();

                Integer refItemIndex = refItemsVisited.get(otherItem);
                if (refItemIndex != null && (foundRefItemIndex == -1 || refItemIndex < foundRefItemIndex)) {
                    foundOtherKey = entry.getKey();
                    foundRefItem = otherItem;
                    foundRefItemIndex = refItemIndex;
                }

                otherItemsVisited.get(entry.getKey()).add(otherItem);
            }

            if (foundRefItem != null) {
                setResult(foundOtherKey, foundRefItem);
            }

            return !nonExhausted.isEmpty();
        }


        private Map<K, Iterator<T>> findNonExhaustedOthers() {
            return otherIterators.entrySet().stream()
                    .filter(e -> e.getValue().hasNext())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
