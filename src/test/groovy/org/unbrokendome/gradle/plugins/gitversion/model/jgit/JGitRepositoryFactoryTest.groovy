package org.unbrokendome.gradle.plugins.gitversion.model.jgit

import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject


class JGitRepositoryFactoryTest extends Specification {

    @Rule
    TemporaryFolder repositoryDir

    @Subject
    repositoryFactory = new JGitRepositoryFactory()

    @Before
    void initRepository() {
        Git.init()
                .setDirectory(repositoryDir.root)
                .call()
    }

    def "Should create repository object for working directory"() {
        when:
            def repository = repositoryFactory.getRepository(repositoryDir.root)
        then:
            repository.workingDir == repositoryDir.root
    }

    def "Should create repository object for .git directory"() {
        given:
            def gitDir = new File(repositoryDir.root, ".git")
        when:
            def repository = repositoryFactory.getRepository(gitDir)
        then:
            repository.workingDir == repositoryDir.root
    }

    def "Should search upwards for repository directory"() {
        given:
            def subDir = repositoryDir.newFolder("subdir")
        when:
            def repository = repositoryFactory.getRepository(subDir)
        then:
            repository.workingDir == repositoryDir.root
    }
}
