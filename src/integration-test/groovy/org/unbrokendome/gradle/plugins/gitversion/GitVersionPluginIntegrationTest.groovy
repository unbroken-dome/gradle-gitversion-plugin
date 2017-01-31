package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import spock.lang.Specification

import java.nio.file.Files


/**
 * Integration test with a "real" Git repository. Creates a bare git repo and makes some commits to it, the first
 * commit being the build.gradle file that's using the plugin.
 *
 * <p>These tests use the Gradle TestKit. If you run the tests from the IDE, make sure to execute the task
 * "pluginUnderTestMetadata" before.
 */
class GitVersionPluginIntegrationTest extends Specification {

    @Rule TestRepository testRepository


    static final String BUILD_FILE_PLUGIN_AND_RULES = '''
        plugins {
            id 'org.unbroken-dome.gitversion'
        }
        
        gitVersion.rules {
            onBranch('master') {
                def branchPoint = branchPoint ~/.*\\/origin\\/release\\/(\\d+)\\.(\\d+)/
                version.major = branchPoint ? branchPoint.matches[1].toInteger() : 0
                version.minor = branchPoint ? branchPoint.matches[2].toInteger() : 0
                version.incrementMinor()
                version.patch = 0
                version.prereleaseTag = 'master'
            }
            
            onBranch(~/release\\/(\\d+)\\.(\\d+)/) {
                version.set(
                    matches[1].toInteger(),
                    matches[2].toInteger(),
                    countCommitsSince(branchPoint()))
            }
        }'''


    static final String BUILD_FILE_PRINT_VERSION = BUILD_FILE_PLUGIN_AND_RULES + '''

        version = gitVersion.determineVersion()
        
        task printVersion {
            doLast { println project.version }
        }
    '''


    private void setupRepository(String buildFileContents) {
        /*
         * Commit graph:
         *
         *   A ----> B ----> D ----> F ----> G
         *            \       \             /
         *             \       \           /
         *              \       \         /
         *               C ----> E ------/
         */
        testRepository.setup { git ->
            git
            // commit A
                    .commitFile('build.gradle', 'initial commit on master', buildFileContents)
            // commit B
                    .commitFile('1', 'second commit on master before branching')
            // commit C
                    .checkoutNew('release/1.0')
                    .commitFile('2', 'first commit on release branch')
            // commit D
                    .checkout('master')
                    .commitFile('3', 'post-branch commit on master')
            // commit E (merge)
                    .checkout('release/1.0')
                    .merge('master', 'merge master into release branch')
            // commit F
                    .checkout('master')
                    .commitFile('4', 'commit on master after master was merged to release')
            // commit G (merge)
                    .merge('release/1.0', 'merge release branch into master')
        }
    }


    def "master branch"() {
        given:
            setupRepository(BUILD_FILE_PRINT_VERSION)
            def workingDir = testRepository.cloneAndCheckout('master')

        when:
            def buildResult = GradleRunner.create()
                    .withProjectDir(workingDir)
                    .withArguments('printVersion', '--stacktrace', '-q')
                    .withPluginClasspath()
                    .withDebug(true)
                    .build()

        then:
            buildResult.task(':printVersion').outcome == TaskOutcome.SUCCESS
        and:
            buildResult.output.trim() == '1.1.0-master'
    }


    def "release branch"() {
        given:
            setupRepository(BUILD_FILE_PRINT_VERSION)
            def workingDir = testRepository.cloneAndCheckout('release/1.0')

        when:
            def buildResult = GradleRunner.create()
                    .withProjectDir(workingDir)
                    .withArguments('printVersion', '--stacktrace', '-q')
                    .withPluginClasspath()
                    .withDebug(true)
                    .build()

        then:
            buildResult.task(':printVersion').outcome == TaskOutcome.SUCCESS
        and:
            buildResult.output.trim() == '1.0.2'
    }


    def "determineGitVersion task"() {
        given:
            setupRepository(BUILD_FILE_PLUGIN_AND_RULES)
            def workingDir = testRepository.cloneAndCheckout('master')

        when:
            def buildResult = GradleRunner.create()
                .withProjectDir(workingDir)
                .withArguments('determineGitVersion', '--stacktrace')
                .withPluginClasspath()
                .withDebug(true)
                .build()

        then:
            buildResult.task(':determineGitVersion').outcome == TaskOutcome.SUCCESS
        and:
            def versionFile = workingDir.toPath().resolve('build/gitversion/gitversion')
            Files.exists(versionFile)
            versionFile.readLines() == [ '1.1.0-master' ]
    }


    def "showGitVersion task"() {
        given:
            setupRepository(BUILD_FILE_PLUGIN_AND_RULES)
            def workingDir = testRepository.cloneAndCheckout('master')

        when:
            def buildResult = GradleRunner.create()
                    .withProjectDir(workingDir)
                    .withArguments('showGitVersion', '--stacktrace')
                    .withPluginClasspath()
                    .withDebug(true)
                    .build()

        then:
            buildResult.task(':determineGitVersion').outcome == TaskOutcome.SUCCESS
        and:
            buildResult.output.readLines().contains '1.1.0-master'
    }
}
