package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableGitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOUtils;


public class JGitRepositoryFactory implements GitRepositoryFactory {

    private final Logger logger = LoggerFactory.getLogger(JGitRepositoryFactory.class);


    @Override
    public CloseableGitRepository getRepository(File dir) {
        return IOUtils.unchecked(() -> doGetRepository(dir));
    }


    private CloseableGitRepository doGetRepository(File dir) throws IOException {

        logger.debug("Trying to open Git repository for directory {}", dir);

        Repository repository = new RepositoryBuilder()
                .findGitDir(dir)
                .build();

        logger.debug("Loaded Git repository: {}", repository);

        return new JGitRepository(repository);
    }
}
