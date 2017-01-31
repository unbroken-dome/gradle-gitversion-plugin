package org.unbrokendome.gradle.plugins.gitversion.model

import spock.lang.Specification

class MockGitRepositoryTest extends Specification {

    /*
     * A ---> B ---> C ---> F ---> G ---> H   (master)
     *         \      \           /
     *          \      \         /
     *           D ---> E ------/             (release)
     */
    def "walk"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .commit('B')
                    .checkoutNew('release')
                    .checkout('master')
                    .commit('C')
                    .checkout('release')
                    .commit('D')
                    .merge('master', 'E')
                    .checkout('master')
                    .commit('F')
                    .merge('release', 'G')
                    .commit('H')
                    .build()
        when:
            def commits = repository.walk().collect { it.message }
        then: "commits should be topologically sorted (children before parents)"
            commits.indexOf('H') < commits.indexOf('G')
            commits.indexOf('G') < commits.indexOf('F')
            commits.indexOf('G') < commits.indexOf('E')
            commits.indexOf('F') < commits.indexOf('C')
            commits.indexOf('E') < commits.indexOf('D')
            commits.indexOf('E') < commits.indexOf('C')
            commits.indexOf('D') < commits.indexOf('B')
            commits.indexOf('C') < commits.indexOf('B')
            commits.indexOf('B') < commits.indexOf('A')
    }


    /*
     * A ---> B ---> C ---> F ---> G       (master)
     *         \      \           /
     *          \      \         /
     *           D ---> E ------/---> H    (release)
     */
    def "first-parent walk"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .commit('B')
                    .checkoutNew('release')
                    .checkout('master')
                    .commit('C')
                    .checkout('release')
                    .commit('D')
                    .merge('master', 'E')
                    .checkout('master')
                    .commit('F')
                    .merge('release', 'G')
                    .checkout('release')
                    .commit('H')
                    .build()
        when:
            def branchHead = repository.getBranch(branchName).head
            def commits = repository.firstParentWalk(branchHead).collect { it.message }
        then:
            commits == expectedCommits
        where:
            branchName | expectedCommits
            'master'   | [ 'G', 'F', 'C', 'B', 'A' ]
            'release'  | [ 'H', 'E', 'D', 'B', 'A' ]
    }
}
