package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.unbrokendome.gradle.plugins.gitversion.model.MockGitRepositoryFactory
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject


class GitVersionExtensionTest extends Specification {

    @Shared
    Project project = ProjectBuilder.builder().build()

    String branchName = null

    def gitRepositoryFactory = new MockGitRepositoryFactory({ branchName })


    @Subject
    gitVersion = new GitVersionExtension(project, gitRepositoryFactory)


    def "determineVersion without any configuration should return default version"() {
        when:
            def version = gitVersion.determineVersion()
        then:
            version == SemVersion.create(0, 1, 0)
    }


    def "determineVersion without rules should return base version"() {
        given:
            gitVersion.rules.baseVersion = '1.2.3'
        when:
            def version = gitVersion.determineVersion()
        then:
            version == SemVersion.create(1, 2, 3)
    }


    def "determineVersion should apply rules"() {
        given:
            branchName = 'master'
        and:
            gitVersion.rules.onBranch('master') {
                version.set(1, 2, 3)
            }
        when:
            def version = gitVersion.determineVersion()
        then:
            version == SemVersion.create(1, 2, 3)
    }
}
