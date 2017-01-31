package org.unbrokendome.gradle.plugins.gitversion.version

import spock.lang.Specification


class SemVersionParserTest extends Specification {

    def "major version only"() {
        when:
            def version = SemVersionParser.parse('1')
        then:
            version == SemVersion.create(1, 0, 0)
    }

    def "major with prerelease tag"() {
        when:
            def version = SemVersionParser.parse('1-alpha')
        then:
            version == SemVersion.create(1, 0, 0, 'alpha')
    }

    def "major with build metadata"() {
        when:
            def version = SemVersionParser.parse('1+xyz')
        then:
            version == SemVersion.create(1, 0, 0, null, 'xyz')
    }

    def "major with prerelease tag and build metadata"() {
        when:
            def version = SemVersionParser.parse('1-alpha+xyz')
        then:
            version == SemVersion.create(1, 0, 0, 'alpha', 'xyz')
    }

    def "major.minor version"() {
        when:
            def version = SemVersionParser.parse('1.2')
        then:
            version == SemVersion.create(1, 2, 0)
    }

    def "major.minor with prerelease tag"() {
        when:
            def version = SemVersionParser.parse('1.2-alpha')
        then:
            version == SemVersion.create(1, 2, 0, 'alpha')
    }

    def "major.minor with build metadata"() {
        when:
            def version = SemVersionParser.parse('1.2+xyz')
        then:
            version == SemVersion.create(1, 2, 0, null, 'xyz')
    }

    def "major.minor with prerelease tag and build metadata"() {
        when:
            def version = SemVersionParser.parse('1.2-alpha+xyz')
        then:
            version == SemVersion.create(1, 2, 0, 'alpha', 'xyz')
    }

    def "major.minor.patch version"() {
        when:
            def version = SemVersionParser.parse('1.2.3')
        then:
            version == SemVersion.create(1, 2, 3)
    }

    def "major.minor.patch with prerelease tag"() {
        when:
            def version = SemVersionParser.parse('1.2.3-alpha')
        then:
            version == SemVersion.create(1, 2, 3, 'alpha')
    }

    def "major.minor.patch with build metadata"() {
        when:
            def version = SemVersionParser.parse('1.2.3+xyz')
        then:
            version == SemVersion.create(1, 2, 3, null, 'xyz')
    }

    def "major.minor.patch with prerelease tag and build metadata"() {
        when:
            def version = SemVersionParser.parse('1.2.3-alpha+xyz')
        then:
            version == SemVersion.create(1, 2, 3, 'alpha', 'xyz')
    }
}
