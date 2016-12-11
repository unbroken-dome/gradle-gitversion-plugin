package org.unbrokendome.gradle.plugins.gitversion

import groovy.util.logging.Log4j2
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.transport.RefSpec
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

@Log4j2
class GitVersionPluginIntegrationTest extends Specification {

    @Rule TemporaryFolder testRepositoryDir
    @Rule TemporaryFolder setupWorkingDir

    @Rule TemporaryFolder testWorkingDir

    static final String buildFileContents = '''
        plugins {
            id 'org.unbroken-dome.gitversion'
        }
        gitVersion {
            onBranch('master') {
                def branchPoint = firstBranchPoint ~/.*\\/origin\\/release\\/(\\d+)\\.(\\d+)/
                def major = branchPoint ? branchPoint.matches[1].toInteger() : 0
                def minor = branchPoint ? branchPoint.matches[2].toInteger() : 0
                project.version = semver(major, minor, 0)
                        .incrementMinorVersion()
                        .setPreReleaseVersion('master')
                        .setBuildMetadata(head.id(7))
                        .toString()
            }
            onBranch(/release\\/(\\d+)\\.(\\d+)/) {
                def major = matches[1].toInteger()
                def minor = matches[2].toInteger()
                def patch = countCommitsSince branchPoint()
                project.version = semver(major, minor, patch)
                        .setBuildMetadata(head.id(7))
            }
        }.doLast { println project.version }
'''

    private Map<String, ObjectId> commits = [:]

    @Before
    void setupRepository() {
        Git.init()
                .setDirectory(testRepositoryDir.root)
                .setBare(true)
                .call()

        def git = Git.cloneRepository()
                .setDirectory(setupWorkingDir.root)
                .setURI(testRepositoryDir.root.toURI().toString())
                .call()

        // commit A: create a 'build.gradle' file as the initial commit on master
        setupWorkingDir.newFile('build.gradle').withWriter { w ->
            w.write(buildFileContents)
        }
        git.add()
                .addFilepattern('build.gradle')
                .call()
        def commit = git.commit()
                .setMessage('initial commit on master')
                .call()
        log.info('Committed: {} {}', commit.abbreviate(7).name(), commit.shortMessage)
        commits.A = commit

        // commit B: change on master (this will be the "branch point")
        commits.B = commitFile(git, '2', 'second commit on master before branching')

        // create a release branch
        git.checkout().setName('release/1.0').setCreateBranch(true).call()

        // commit C: change on release branch
        commits.C = commitFile(git, '3', 'first commit on release branch')

        // commit D: change on master branch
        git.checkout().setName('master').call()
        commits.D = commitFile(git, '4', 'post-branch commit on master')

        // commit E: merge master into release branch
        git.checkout().setName('release/1.0').call()
        commits.E = merge(git, 'master', 'merge master into release branch')

        // commit F: change on master
        git.checkout().setName('master').call()
        commits.F = commitFile(git, '5', 'commit on master after master was merged to release')

        // commit G: merge release branch into master
        commits.G = merge(git, 'release/1.0', 'merge release branch into master')
        git.push()
                .setRemote('origin')
                .setRefSpecs(
                        new RefSpec('master:master'),
                        new RefSpec('release/1.0:release/1.0'))
                .call()
    }

    private ObjectId commitFile(Git git, String fileName, String message) {
        setupWorkingDir.newFile(fileName)
        git.add()
                .addFilepattern(fileName)
                .call()
        def commit = git.commit()
                .setMessage(message)
                .call()
        log.info('Committed: {} {}', commit.abbreviate(7).name(), commit.shortMessage)
        return commit
    }

    private ObjectId merge(Git git, String from, String message) {
        def mergeResult = git.merge()
                .include(git.repository.findRef(from))
                .setCommit(true)
                .setMessage(message)
                .call()
        log.info("Merge commit: {} ({} -> {}) {}",
                mergeResult.newHead.abbreviate(7).name(), from, git.repository.branch, message)
        return mergeResult.newHead
    }


    def "master branch"() {
        given:
            Git.cloneRepository()
                .setDirectory(testWorkingDir.root)
                .setURI(testRepositoryDir.root.toURI().toString())
                .setBranch('master')
                .call()

        when:
            def buildResult = GradleRunner.create()
                    .withProjectDir(testWorkingDir.root)
                    .withArguments('gitVersion', '--stacktrace', '-q')
                    .withPluginClasspath()
                    .withDebug(true)
                    .build()

        then:
            buildResult.task(':gitVersion').outcome == TaskOutcome.SUCCESS
            buildResult.output.trim() == '1.1.0-master+' + commits.G.abbreviate(7).name()
    }


    def "release branch"() {
        given:
            Git.cloneRepository()
                .setDirectory(testWorkingDir.root)
                .setURI(testRepositoryDir.root.toURI().toString())
                .setBranch('release/1.0')
                .call()

        when:
            def buildResult = GradleRunner.create()
                    .withProjectDir(testWorkingDir.root)
                    .withArguments('gitVersion', '--stacktrace', '-q')
                    .withPluginClasspath()
                    .withDebug(true)
                    .build()

        then:
            buildResult.task(':gitVersion').outcome == TaskOutcome.SUCCESS
            buildResult.output.trim() == '1.0.2+' + commits.E.abbreviate(7).name()
    }
}
