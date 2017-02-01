package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.io.File;

import javax.annotation.Nullable;

import org.gradle.api.Project;
import org.gradle.api.logging.Logging;
import org.slf4j.Logger;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableGitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryWithBranchName;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;


public class Versioning {

    private final Logger logger = Logging.getLogger(Versioning.class);

    private final Project project;
    private final RulesContainerInternal rules;
    private final GitRepositoryFactory gitRepositoryFactory;

    @Nullable
    private final String overrideBranchName;

    @Nullable
    private final File gitDirectory;


    public Versioning(Project project,
                      RulesContainerInternal rules,
                      GitRepositoryFactory gitRepositoryFactory,
                      @Nullable String overrideBranchName,
                      @Nullable File gitDirectory) {
        this.project = project;
        this.rules = rules;
        this.gitRepositoryFactory = gitRepositoryFactory;
        this.overrideBranchName = overrideBranchName;
        this.gitDirectory = gitDirectory;
    }


    public SemVersion determineVersion() {

        if (gitDirectory == null) {
            SemVersion version = rules.getBaseVersion().toImmutable();
            logger.info("Git directory is not set; defaulting to base version {}", version);
            return version;
        }

        try (CloseableGitRepository gitRepository = gitRepositoryFactory.getRepository(gitDirectory)) {

            /* If the branch should be overridden, decorate the GitRepository */
            GitRepository actualGitRepository;
            if (overrideBranchName != null) {
                logger.info("Overriding branch name with \"{}\"", overrideBranchName);
                actualGitRepository = new GitRepositoryWithBranchName(gitRepository, overrideBranchName);
            } else {
                actualGitRepository = gitRepository;
            }

            logRepositoryInformation(actualGitRepository);

            VersioningRules versioningRules = rules.getVersioningRules();
            return versioningRules.evaluate(project, actualGitRepository);
        }
    }


    private void logRepositoryInformation(GitRepository gitRepository) {
        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder("Git repository information")
                    .append("\n    work tree:      ")
                    .append(gitRepository.getWorkingDir());

            builder.append("\n    current branch: ");
            GitBranch currentBranch = gitRepository.getCurrentBranch();
            if (currentBranch != null) {
                builder.append(currentBranch.getFullName());
            } else {
                builder.append("<not set>");
            }

            builder.append("\n    HEAD commit:    ");
            GitCommit head = gitRepository.getHead();
            if (head != null) {
                builder.append(head.id(16));
            } else {
                builder.append("<not set>");
            }

            logger.debug(builder.toString());
        }
    }
}
