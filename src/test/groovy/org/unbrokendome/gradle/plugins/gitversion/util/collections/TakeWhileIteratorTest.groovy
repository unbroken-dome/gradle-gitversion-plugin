package org.unbrokendome.gradle.plugins.gitversion.util.collections

import com.google.common.collect.ImmutableList
import spock.lang.Specification


class TakeWhileIteratorTest extends Specification {

    def "Iterator should return items until condition returns false"() {
        given:
            def items = [ 1, 2, 3, 2, 1 ]
        when:
            def iterator = new TakeWhileIterator(items.iterator(), { it < 3 })
            def iteratedItems = ImmutableList.copyOf(iterator)
        then:
            iteratedItems == [ 1, 2 ]
    }
}
