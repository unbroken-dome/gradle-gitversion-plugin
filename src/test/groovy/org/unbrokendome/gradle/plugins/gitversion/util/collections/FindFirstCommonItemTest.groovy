package org.unbrokendome.gradle.plugins.gitversion.util.collections

import spock.lang.Specification


class FindFirstCommonItemTest extends Specification {

    def "findFirstCommonItem with map"() {
        given:
            def ref = [ 1, 2, 3, 4, 5 ]
            def other = [ 'A': [ 3, 7 ], 'B': [ 2, 3, 8 ] ]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result.toList() == [ 'B', 2 ]
    }


    def "findFirstCommonItem with one other"() {
        given:
            def ref = [ 1, 3, 5, 7, 8, 9, 11 ]
            def other = [ 2, 4, 6, 8, 10, 12 ]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 8
    }


    def "findFirstCommonItem with two others"() {
        given:
            def ref = [ 5, 3, 1 ]
            def others = [ 'A': [ 2, 1 ], 'B': [ 4, 3, 1 ] ]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, others)
        then:
            result.toList() == [ 'B', 3 ]
    }


    def "no common item"() {
        given:
            def ref = [ 1, 2, 3, 4, 5 ]
            def other = [ 6, 7, 8, 9, 10 ]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == null
    }


    def "multiple common items"() {
        given:
            def ref = [4, 3, 2, 1]
            def other = [7, 6, 5, 2, 1]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 2
    }


    def "common item after reference set is exhausted"() {
        given:
            def ref = [4, 2, 1]
            def other = [8, 7, 6, 5, 2, 1]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 2
    }


    def "common item after other set is exhausted"() {
        given:
            def ref = [8, 7, 6, 5, 4, 3]
            def other = [4, 3]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 4
    }
}
