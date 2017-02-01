package org.unbrokendome.gradle.plugins.gitversion;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.util.ConfigureUtil;
import org.unbrokendome.gradle.plugins.gitversion.core.RulesContainer;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.internal.DefaultRulesContainer;
import org.unbrokendome.gradle.plugins.gitversion.internal.LazyVersion;
import org.unbrokendome.gradle.plugins.gitversion.internal.RulesContainerInternal;
import org.unbrokendome.gradle.plugins.gitversion.internal.Versioning;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableGitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryWithBranchName;
import org.unbrokendome.gradle.plugins.gitversion.tasks.DetermineGitVersion;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

/**
 * DSL extension for the {@code gitVersion} block.
 */
public class GitVersionExtension {

    private final Project project;
    private final GitRepositoryFactory gitRepositoryFactory;
    private final RulesContainerInternal rules = new DefaultRulesContainer();
    private String overrideBranchName;


    public GitVersionExtension(Project project, GitRepositoryFactory gitRepositoryFactory) {
        this.project = project;
        this.gitRepositoryFactory = gitRepositoryFactory;
    }


    /**
     * Gets the container object that represents the versioning rules.
     * @return the {@link RulesContainer}
     */
    @Nonnull
    public RulesContainer getRules() {
        return rules;
    }


    /**
     * Configures the {@linkplain #getRules() rules} using an action.
     * @param action the configuration {@link Action}
     */
    public void rules(Action<RulesContainer> action) {
        action.execute(rules);
    }


    /**
     * Configures the {@link #getRules() rules} container using a closure.
     * @param closure the configuration closure, executed in the context of the {@link RulesContainer}
     */
    public void rules(@DelegatesTo(RulesContainer.class) Closure closure) {
        rules(ConfigureUtil.configureUsing(closure));
    }


    @Nonnull
    public SemVersion determineVersion() {
        Versioning versioning = new Versioning(project,
                rules,
                gitRepositoryFactory,
                overrideBranchName,
                project.getProjectDir());

        return versioning.determineVersion();
    }


    /**
     * Gets the version that was written to a file by an earlier invocation of
     * a {@link org.unbrokendome.gradle.plugins.gitversion.tasks.DetermineGitVersion DetermineGitVersion} task.
     * @return the cached version, or {@code null} if the version file does not exist
     * @throws IOException on I/O errors
     */
    @Nullable
    public SemVersion getCachedVersion() throws IOException {
        DetermineGitVersion determineVersionTask = (DetermineGitVersion)
                project.getTasks().findByName(GitVersionPlugin.DETERMINE_GITVERSION_TASK_NAME);
        if (determineVersionTask != null) {
            return determineVersionTask.getCachedVersion();
        }
        return null;
    }


    /**
     * Returns a lazily-evaluated object that can be assigned to the {@link Project#setVersion(Object) Project.version}
     * property. The version is only calculated the first time its {@link Object#toString() toString()} method is
     * called.
     *
     * <p>If at the time of evaluation, the version cannot be determined, the {@code toString()} method will return
     * an empty string.</p>
     *
     * @return a lazily-evaluated version
     */
    @Nonnull
    public Object getLazyVersion() {
        return new LazyVersion(this::determineVersion);
    }


    /**
     * Gets the branch name that will be used for versioning.
     *
     * @return the branch name, or {@code null} if the repository HEAD is used
     */
    @Nullable
    public String getOverrideBranchName() {
        return overrideBranchName;
    }


    /**
     * Sets the branch name that should be used for versioning.
     *
     * This is intended for certain situations
     * (like running on Jenkins CI) where it is necessary to pretend being on another branch than the
     * repository actually is.
     *
     * <p>By default (if this property is {@code null}), the Git repository's HEAD branch is used.</p>
     *
     * @param overrideBranchName the branch name, or {@code null} to use the repository HEAD
     */
    public void setOverrideBranchName(@Nullable String overrideBranchName) {
        this.overrideBranchName = overrideBranchName;
    }
}
