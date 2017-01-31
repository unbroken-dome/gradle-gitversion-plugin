package org.unbrokendome.gradle.plugins.gitversion;

import java.io.File;

import javax.annotation.Nonnull;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.unbrokendome.gradle.plugins.gitversion.model.EnvironmentAwareGitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.model.jgit.JGitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.tasks.DetermineGitVersion;
import org.unbrokendome.gradle.plugins.gitversion.tasks.ShowGitVersion;


public class GitVersionPlugin implements Plugin<Project> {

    public static final String GITVERSION_EXTENSION_NAME = "gitVersion";

    static final String DETERMINE_GITVERSION_TASK_NAME = "determineGitVersion";
    static final String SHOW_GITVERSION_TASK_NAME = "showGitVersion";


    @Override
    public void apply(Project project) {

        GitVersionExtension extension = installGitVersionExtension(project);

        DetermineGitVersion determineVersionTask = createDetermineVersionTask(project, extension);
        createShowGitVersionTask(project, determineVersionTask);
    }


    private GitVersionExtension installGitVersionExtension(Project project) {
        GitRepositoryFactory gitRepositoryFactory =
                new EnvironmentAwareGitRepositoryFactory(new JGitRepositoryFactory());

        GitVersionExtension extension = new GitVersionExtension(project, gitRepositoryFactory);
        project.getExtensions().add(GITVERSION_EXTENSION_NAME, extension);

        return extension;
    }


    @Nonnull
    private DetermineGitVersion createDetermineVersionTask(Project project, GitVersionExtension extension) {
        DetermineGitVersion determineVersionTask = project.getTasks().create(
                DETERMINE_GITVERSION_TASK_NAME, DetermineGitVersion.class);

        determineVersionTask.conventionMapping("repositoryLocation",
                project::getProjectDir);
        determineVersionTask.conventionMapping("targetFile",
                () -> new File(project.getBuildDir(), "gitversion/gitversion"));
        determineVersionTask.conventionMapping("rules",
                extension::getRules);
        return determineVersionTask;
    }


    private ShowGitVersion createShowGitVersionTask(Project project, DetermineGitVersion determineVersionTask) {
        ShowGitVersion showGitVersionTask = project.getTasks().create(
                SHOW_GITVERSION_TASK_NAME, ShowGitVersion.class);
        showGitVersionTask.conventionMapping("fromTask",
                () -> determineVersionTask);

        return showGitVersionTask;
    }
}
