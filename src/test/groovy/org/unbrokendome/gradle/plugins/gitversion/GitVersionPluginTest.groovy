package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GitVersionPluginTest extends Specification {

    def project = ProjectBuilder.builder().build()

    def "Should create gitVersion task"() {
        when:
        project.apply plugin: GitVersionPlugin

        then:
        project.tasks.findByName('gitVersion') instanceof GitVersionTask
    }
}
