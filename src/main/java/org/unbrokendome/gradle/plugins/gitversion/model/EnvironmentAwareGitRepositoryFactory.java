package org.unbrokendome.gradle.plugins.gitversion.model;

import java.io.File;


public class EnvironmentAwareGitRepositoryFactory implements GitRepositoryFactory {

    private static final String BRANCH_NAME_ENV_VAR = "BRANCH_NAME";

    private final GitRepositoryFactory gitRepositoryFactory;


    public EnvironmentAwareGitRepositoryFactory(GitRepositoryFactory gitRepositoryFactory) {
        this.gitRepositoryFactory = gitRepositoryFactory;
    }


    @Override
    public CloseableGitRepository getRepository(File dir) {
        CloseableGitRepository gitRepository = gitRepositoryFactory.getRepository(dir);

        String branchFromEnvironment = System.getenv(BRANCH_NAME_ENV_VAR);
        if (branchFromEnvironment != null) {
            return new GitRepositoryWithBranchName(gitRepository, branchFromEnvironment);
        } else {
            return gitRepository;
        }
    }
}
