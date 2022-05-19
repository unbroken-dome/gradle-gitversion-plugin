package org.unbrokendome.gradle.plugins.gitversion.model.jgit

import org.eclipse.jgit.api.Git
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.TempDir

class JGitRepositoryFactoryTest extends Specification {

    @TempDir
    File repositoryDir

    @Subject
    repositoryFactory = new JGitRepositoryFactory()

    def setup() {
        Git.init()
                .setDirectory(repositoryDir)
                .call()
    }

    def "Should create repository object for working directory"() {
        when:
            def repository = repositoryFactory.getRepository(repositoryDir)
        then:
            repository.workingDir == repositoryDir
    }

    def "Should create repository object for .git directory"() {
        given:
            def gitDir = new File(repositoryDir, ".git")
        when:
            def repository = repositoryFactory.getRepository(gitDir)
        then:
            repository.workingDir == repositoryDir
    }

    def "Should search upwards for repository directory"() {
        given:
            def subDir = new File(repositoryDir, "subdir")
            subDir.mkdir()
        when:
            def repository = repositoryFactory.getRepository(subDir)
        then:
            repository.workingDir == repositoryDir
    }
}
