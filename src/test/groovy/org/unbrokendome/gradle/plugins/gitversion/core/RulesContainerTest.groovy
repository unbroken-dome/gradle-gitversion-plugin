package org.unbrokendome.gradle.plugins.gitversion.core

import org.gradle.testfixtures.ProjectBuilder
import org.unbrokendome.gradle.plugins.gitversion.internal.DefaultRulesContainer
import org.unbrokendome.gradle.plugins.gitversion.model.MockGitRepository
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject


class RulesContainerTest extends Specification {

    @Subject
    rules = new DefaultRulesContainer()

    @Shared project = ProjectBuilder.builder().build()

    String branchName = null


    def "baseVersion can be set as string"() {
        when:
            rules.baseVersion = '1.2.3'
        then:
            rules.baseVersion.toImmutable() == SemVersion.create(1, 2, 3)
    }


    def "'always' rule is always evaluated"() {
        given:
            rules.always { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "'before' rule is always evaluated"() {
        given:
            rules.before { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "'before' rule is evaluated first"() {
        given:
            rules.always { version.set(4, 5, 6) }
            rules.before { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(4, 5, 6)
    }


    def "'after' rule is always evaluated"() {
        given:
            rules.after { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "'after' rule is evaluated last"() {
        given:
            rules.after { version.set(1, 2, 3) }
            rules.always { version.set(4, 5, 6) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "following rules are not evaluated if skipOtherRules is set"() {
        given:
            rules.always {
                version.set(1, 2, 3)
                skipOtherRules = true
            }
            rules.always {
                version.set(4, 5, 6)
            }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "detached head rule is not evaluated when current branch is set"() {
        given:
            branchName = 'master'
        and:
            rules.always { version.set(1, 2, 3) }
            rules.onDetachedHead { version.set(4, 5, 6) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "detached head rule is evaluated when current branch is not set"() {
        given:
            branchName = null
        and:
            rules.onDetachedHead { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "branch rule is evaluated when current branch name matches string"() {
        given:
            branchName = 'master'
        and:
            rules.onBranch('master') { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "branch rule is not evaluated when current branch name does not match"() {
        given:
            branchName = 'develop'
        and:
            rules.always { version.set(1, 2, 3) }
            rules.onBranch('master') { version.set(4, 5, 6) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "branch rule is evaluated when current branch name matches pattern"() {
        given:
            branchName = 'release/1.2'
        and:
            rules.onBranch(~/release\/\d+\.\d+/) { version.set(1, 2, 3) }
        when:
            def version = evaluateRules()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    private SemVersion evaluateRules() {
        def gitRepository = MockGitRepository.builder().with {
            workingDir = project.projectDir
            commit('initial commit')
            if (branchName) {
                checkoutNew(branchName)
            } else {
                detach()
            }
            build()
        }
        rules.versioningRules.evaluate(project, gitRepository)
    }
}
