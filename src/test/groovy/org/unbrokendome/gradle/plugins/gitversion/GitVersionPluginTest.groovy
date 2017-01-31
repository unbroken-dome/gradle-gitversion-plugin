package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


class GitVersionPluginTest extends Specification {

    def project = ProjectBuilder.builder().build()


    def "Plugin should install gitVersion extension"() {
        when:
            project.apply plugin: GitVersionPlugin
        then:
            project.gitVersion instanceof GitVersionExtension
    }
}
