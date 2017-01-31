package org.unbrokendome.gradle.plugins.gitversion.model;

import java.io.File;
import java.util.Collections;
import java.util.function.Supplier;


public class MockGitRepositoryFactory implements GitRepositoryFactory {

    private final Supplier<String> branchNameSupplier;


    public MockGitRepositoryFactory(String branchName) {
        this(() -> branchName);
    }


    public MockGitRepositoryFactory(Supplier<String> branchNameSupplier) {
        this.branchNameSupplier = branchNameSupplier;
    }


    @Override
    public CloseableGitRepository getRepository(File dir) {
        return MockGitRepository.builder()
                .setWorkingDir(dir)
                .checkoutNew("master")
                .commit("initial commit")
                .build();
    }
}
