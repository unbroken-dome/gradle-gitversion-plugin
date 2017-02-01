package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import spock.lang.Specification

import java.nio.file.Files
import java.util.regex.Pattern


/**
 * Integration test with a "real" Git repository. Creates a bare git repo and makes some commits to it, the first
 * commit being the build.gradle file that's using the plugin.
 *
 * <p>These tests use the Gradle TestKit. If you run the tests from the IDE, make sure to execute the task
 * "pluginUnderTestMetadata" before.
 */
class GitVersionPluginIntegrationTest extends Specification {

    @Rule TestRepository testRepository

    private BuildResult buildResult


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


    static final String BUILD_FILE_OVERRIDE_BRANCH_NAME = BUILD_FILE_PLUGIN_AND_RULES + '''
        gitVersion.overrideBranchName = project.getProperty('gitBranch')
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
            testRepository.cloneAndCheckout('master')

        when:
            runGradleBuild('printVersion', '-q')

        then:
            buildResult.task(':printVersion').outcome == TaskOutcome.SUCCESS
        and:
            findSystemOutLines() == [ '1.1.0-master' ]
    }


    def "release branch"() {
        given:
            setupRepository(BUILD_FILE_PRINT_VERSION)
            testRepository.cloneAndCheckout('release/1.0')

        when:
            runGradleBuild('printVersion', '-q')

        then:
            buildResult.task(':printVersion').outcome == TaskOutcome.SUCCESS
        and:
            findSystemOutLines() == [ '1.0.2' ]
    }


    def "determineGitVersion task"() {
        given:
            setupRepository(BUILD_FILE_PLUGIN_AND_RULES)
            def workingDir = testRepository.cloneAndCheckout('master')

        when:
            runGradleBuild('determineGitVersion')

        then:
            buildResult.task(':determineGitVersion').outcome == TaskOutcome.SUCCESS
        and:
            getContentsFromVersionFile() == '1.1.0-master'
    }


    def "showGitVersion task"() {
        given:
            setupRepository(BUILD_FILE_PLUGIN_AND_RULES)
            def workingDir = testRepository.cloneAndCheckout('master')

        when:
            runGradleBuild('showGitVersion')

        then:
            buildResult.task(':determineGitVersion').outcome == TaskOutcome.SUCCESS
        and:
            findSystemOutLines() == [ '1.1.0-master' ]
    }


    def "detached head and build parameter for branch name"() {
        /* This test uses a project property (= build parameter) to override the branch name
           in a detached-head scenario. We cannot use environment variables with Gradle TestKit,
           but this should be close enough. */
        given:
            setupRepository(BUILD_FILE_OVERRIDE_BRANCH_NAME)
            testRepository.cloneAndCheckout('master')
            testRepository.detach()

        when:
            runGradleBuild('determineGitVersion', '-PgitBranch=master')

        then:
            buildResult.task(':determineGitVersion').outcome == TaskOutcome.SUCCESS
        and:
            getContentsFromVersionFile() == '1.1.0-master'
    }


    def "detached head and build parameter for nonexisting branch name"() {
        /* This test uses a project property (= build parameter) to override the branch name
           in a detached-head scenario. We cannot use environment variables with Gradle TestKit,
           but this should be close enough. */
        given:
        setupRepository(BUILD_FILE_OVERRIDE_BRANCH_NAME)
        testRepository.cloneAndCheckout('master')
        testRepository.detach()

        when:
        runGradleBuild('determineGitVersion', '-PgitBranch=release/2.0')

        then:
        buildResult.task(':determineGitVersion').outcome == TaskOutcome.SUCCESS
        and:
        getContentsFromVersionFile() == '2.0.0'
    }


    private void runGradleBuild(String... arguments) {

        List<String> allArgs = arguments.toList() + [ '--debug', '--stacktrace' ]

        buildResult = GradleRunner.create()
                .withProjectDir(testRepository.workingDir)
                .withArguments(allArgs)
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
    }


    private String getContentsFromVersionFile() {
        def versionFile = testRepository.workingDir.toPath().resolve('build/gitversion/gitversion')
        assert Files.exists(versionFile)
        return versionFile.withReader { it.readLine().trim() }
    }


    private List<String> findSystemOutLines() {
        findLinesInOutput(~/.*\[system\.out] (.*)/)
    }


    private List<String> findLinesInOutput(Pattern pattern) {

        List<String> results = []

        buildResult.output.eachLine {
            def matcher = (it =~ pattern)
            if (matcher.matches()) {
                if (matcher.groupCount() > 0) {
                    results.add(matcher.group(1))
                } else {
                    results.add(matcher.group(0))
                }
            }
            null
        }

        results
    }
}
