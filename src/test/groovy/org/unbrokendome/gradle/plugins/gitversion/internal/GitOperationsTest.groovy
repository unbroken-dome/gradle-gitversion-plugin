package org.unbrokendome.gradle.plugins.gitversion.internal

import org.unbrokendome.gradle.plugins.gitversion.model.MockGitRepository
import spock.lang.Specification
import spock.lang.Unroll

class GitOperationsTest extends Specification {

    /*
     * A ----> C (master)
     *  \
     *   \
     *    \--> B (release, HEAD)
     */
    def "branchPoint for simple branches"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .checkoutNew('release')
                    .commit('B')
                    .checkout('master')
                    .commit('C')
                    .checkout('release')
                    .build()
        when:
            def branchPoint = new GitOperations(repository).branchPoint('master')
        then:
            branchPoint.commit.message == 'A'
    }

    /*
     * A ---> C ---> E      (master)
     *  \      \
     *   \      \
     *    B ---> D ---> F   (release, HEAD)
     */
    def "branchPoint with merge"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .checkoutNew('release')
                    .commit('B')
                    .checkout('master')
                    .commit('C')
                    .checkout('release')
                    .merge('master', 'D')
                    .checkout('master')
                    .commit('E')
                    .checkout('release')
                    .commit('F')
                    .build()
        when:
            def branchPoint = new GitOperations(repository).branchPoint('master')
        then:
            branchPoint.commit.message == 'A'
    }

    /*
     * A ---> C ---> E       (master, HEAD)
     *  \      \
     *   B      \            (release1)
     *           \
     *            D          (release2)
     */
    def "branchPoint with multiple other branches"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .checkoutNew('release1')
                    .commit('B')
                    .checkout('master')
                    .commit('C')
                    .checkoutNew('release2')
                    .commit('D')
                    .checkout('master')
                    .commit('E')
                    .build()
        when:
            def branchPoint = new GitOperations(repository).branchPoint('release1', 'release2')
        then:
            branchPoint.commit.message == 'C'
            branchPoint.otherBranchName == 'release2'
    }

    /*
     * A ---> C ---> E       (master, HEAD)
     *  \      \
     *   B      \            (release1)
     *           \
     *            D          (release2)
     */
    def "branchPoint with branch name pattern"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .checkoutNew('release1')
                    .commit('B')
                    .checkout('master')
                    .commit('C')
                    .checkoutNew('release2')
                    .commit('D')
                    .checkout('master')
                    .commit('E')
                    .build()
        when:
            def branchPoint = new GitOperations(repository).branchPoint(~/release(\d+)/)
        then:
            branchPoint.commit.message == 'C'
            branchPoint.otherBranchName == 'release2'
        and:
            branchPoint.matches.size() == 2
            branchPoint.matches.toList() == [ 'release2', '2' ]
    }

    /*
     * A --> B --> C     (master, HEAD)
     */
    def "countCommitsSince simple case"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .commit('B')
                    .commit('C')
                    .build()
            def commitC = repository.head
            def commitA = commitC.parents.first().parents.first()
        when:
            def count = new GitOperations(repository).countCommitsSince(commitA, false)
        then:
            count == 2
    }

    /*
     * A ---------> D  (master)
     *  \          ^
     *   \        /
     *    B ---> C     (develop)
     */
    @Unroll
    def "countCommitsSince counting merge commits"() {
        given:
            def repository = MockGitRepository.builder()
                .commit('A')
                .checkoutNew('develop')
                .commit('B')
                .commit('C')
                .checkout('master')
                .merge('develop', 'D')
                .build()
            def commitC = repository.head.parents[1].parents.first()
        when:
            def count = new GitOperations(repository).countCommitsSince(commitC, countingMergeCommits)
        then:
            count == expectedCount
        where:
            countingMergeCommits | expectedCount
            true                 | 2
            false                | 1
    }

    /*
     *            (T1)
     * A --> B --> C    (master, HEAD)
     */
    def "findLatestTag should find tag on same commit"() {
        given:
            def repository = MockGitRepository.builder()
                    .commit('A')
                    .commit('B')
                    .commit('C')
                    .tag('T1')
                    .build()
        when:
            def tag = new GitOperations(repository).findLatestTag(~/T(\d+)/, false)
        then:
            tag != null
            tag.tagName == 'T1'
            tag.matches.toList() == [ 'T1', '1' ]
    }


    /*
     *      (T1)
     * A --> B --> C    (master, HEAD)
     */
    def "findLatestTag should find tag on previous commit"() {
        given:
        def repository = MockGitRepository.builder()
                .commit('A')
                .commit('B')
                .tag('T1')
                .commit('C')
                .build()
        when:
            def tag = new GitOperations(repository).findLatestTag(~/T\d+/, false)
        then:
            tag != null
            tag.tagName == 'T1'
    }


    /*
     *      (T1)  (T2)
     * A --> B --> C    (master, HEAD)
     */
    def "findLatestTag should find latest tag if there are multiple matches"() {
        given:
        def repository = MockGitRepository.builder()
                .commit('A')
                .commit('B')
                .tag('T1')
                .commit('C')
                .tag('T2')
                .build()
        when:
            def tag = new GitOperations(repository).findLatestTag(~/T\d+/, false)
        then:
            tag != null
            tag.tagName == 'T2'
    }
}
