package org.unbrokendome.gradle.plugins.gitversion

import spock.lang.Specification

class CollectionUtilsTest extends Specification {

    def "findFirstCommonItem with map"() {
        given:
            def ref = [ 1, 2, 3, 4, 5 ]
            def other = [ 'A': [ 3, 7 ], 'B': [ 2, 3, 8 ] ]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result.first == 'B'
            result.second == 2
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


    def "test"() {
        given:
            def ref = [4, 3, 2, 1]
            def other = [7, 6, 5, 2, 1]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 2
    }


    def "test2"() {
        given:
            def ref = [4, 2, 1]
            def other = [8, 7, 6, 5, 2, 1]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 2
    }


    def "test3"() {
        given:
            def ref = [8, 7, 6, 5, 4, 3]
            def other = [4, 3]
        when:
            def result = CollectionUtils.findFirstCommonItem(ref, other)
        then:
            result == 4
    }
}
