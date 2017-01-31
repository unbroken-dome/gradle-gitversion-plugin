package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.unbrokendome.gradle.plugins.gitversion.tasks.DetermineGitVersion
import org.unbrokendome.gradle.plugins.gitversion.tasks.ShowGitVersion
import spock.lang.Specification

class GitVersionPluginTest extends Specification {

    @Rule TemporaryFolder projectDir

    Project project


    @BeforeClass
    static void disableNativeGradleServices() {
        // This will prevent Gradle from copying a native binding DLL to the test directory,
        // which both speeds up the tests and allows the temp folder to be cleaned up properly
        System.setProperty("org.gradle.native", "false")
    }


    @Before
    void initProject() {
        project = ProjectBuilder.builder()
                .withProjectDir(projectDir.root)
                .build()
    }


    def "Plugin should install gitVersion extension"() {
        when:
            project.apply plugin: GitVersionPlugin
        then:
            project.gitVersion instanceof GitVersionExtension
    }


    def "Plugin should create determineGitVersion task"() {
        when:
            project.apply plugin: GitVersionPlugin
        then:
            project.tasks.findByName('determineGitVersion') != null
    }


    def "Should initialize property determineGitVersion.targetFile"() {
        when:
            project.apply plugin: GitVersionPlugin
        and:
            (project as ProjectInternal).evaluate()
        then:
            def determineGitVersion = project.tasks.getByName('determineGitVersion') as DetermineGitVersion
            determineGitVersion.targetFile == new File(project.buildDir, 'gitversion/gitversion')
    }


    def "Should initialize property determineGitVersion.repositoryLocation"() {
        when:
            project.apply plugin: GitVersionPlugin
        and:
            (project as ProjectInternal).evaluate()
        then:
            def determineGitVersion = project.tasks.getByName('determineGitVersion') as DetermineGitVersion
            determineGitVersion.repositoryLocation == project.projectDir
    }


    def "Should initialize property showGitVersion.fromTask"() {
        when:
            project.apply plugin: GitVersionPlugin
        and:
            (project as ProjectInternal).evaluate()

        then:
            def determineGitVersion = project.tasks.getByName('determineGitVersion') as DetermineGitVersion
            def showGitVersion = project.tasks.getByName('showGitVersion') as ShowGitVersion
            showGitVersion.dependsOn as List == [ determineGitVersion ]
    }
}
