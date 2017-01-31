package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableGitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOUtils;

import java.io.File;
import java.io.IOException;


public class JGitRepositoryFactory implements GitRepositoryFactory {

    @Override
    public CloseableGitRepository getRepository(File dir) {
        return IOUtils.unchecked(() -> doGetRepository(dir));
    }


    private CloseableGitRepository doGetRepository(File dir) throws IOException {
        Repository repository = new RepositoryBuilder()
                .findGitDir(dir)
                .build();
        return new JGitRepository(repository);
    }
}
