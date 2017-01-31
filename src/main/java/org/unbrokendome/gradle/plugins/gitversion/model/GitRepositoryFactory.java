package org.unbrokendome.gradle.plugins.gitversion.model;

import java.io.File;


public interface GitRepositoryFactory {

    CloseableGitRepository getRepository(File dir);
}
