package org.unbrokendome.gradle.plugins.gitversion.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.unbrokendome.gradle.plugins.gitversion.GitVersionExtension;
import org.unbrokendome.gradle.plugins.gitversion.GitVersionPlugin;
import org.unbrokendome.gradle.plugins.gitversion.core.RulesContainer;
import org.unbrokendome.gradle.plugins.gitversion.core.VersioningRules;
import org.unbrokendome.gradle.plugins.gitversion.internal.RulesContainerInternal;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableGitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryFactory;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepositoryWithBranchName;
import org.unbrokendome.gradle.plugins.gitversion.util.RepositoryUtils;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;


/**
 * A task that determines the version from a Git repository and stores it in a file.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DetermineGitVersion extends ConventionTask {

    private File repositoryLocation;
    private File gitDirectory;
    private RulesContainer rules;
    private String overrideBranchName;
    private File targetFile;

    /**
     * Gets the location of the Git repository.
     *
     * @return the full path to the repository
     */
    @Internal
    public File getRepositoryLocation() {
        return repositoryLocation;
    }


    /**
     * Sets the location of the Git repository that will be used for versioning.
     *
     * <p>The repository location may be either the work tree root, any subdirectory under it, or the
     * .git directory. The location of the actual {@linkplain #getGitDirectory() .git directory} will then be
     * determined from this path, and used as an input directory for the task.</p>
     *
     * @param repositoryLocation the full path to the repository
     */
    public void setRepositoryLocation(File repositoryLocation) {
        this.repositoryLocation = repositoryLocation;
        if (repositoryLocation != null) {
            gitDirectory = RepositoryUtils.findGitDirectory(repositoryLocation);
        }
    }


    /**
     * Gets the full path of the .git directory of the Git repository that will be used for versioning.
     * @return the full path of the .git directory
     */
    @InputDirectory
    @Optional
    @Nullable
    public File getGitDirectory() {
        return gitDirectory;
    }


    @Internal
    @Nullable
    public RulesContainer getRules() {
        return rules;
    }


    public void setRules(RulesContainer rules) {
        this.rules = rules;
    }


    /**
     * Gets the branch name that will be used for versioning.
     *
     * @return the branch name, or {@code null} if the repository HEAD is used
     */

    @Input
    @Optional
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
    public void setOverrideBranchName(String overrideBranchName) {
        this.overrideBranchName = overrideBranchName;
    }


    /**
     * Gets the target file to which the version will be written.
     * @return the full path to the target file
     */
    @OutputFile
    public File getTargetFile() {
        return targetFile;
    }


    /**
     * Sets the target file to which the version will be written.
     * @param targetFile the full path to the target file
     */
    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * Gets the version that was written to the {@linkplain #getTargetFile() target file} by a previous invocation
     * of this task.
     *
     * @return a {@link SemVersion} parsed from the contents of the version file that was previously written;
     *         {@code null} if the file does not exist
     * @throws IOException for I/O errors
     */
    @Nullable
    @Internal
    public SemVersion getCachedVersion() throws IOException {
        File targetFile = getTargetFile();
        if (targetFile != null && targetFile.exists()) {
            List<String> lines = Files.readAllLines(targetFile.toPath());
            if (lines != null && !lines.isEmpty()) {
                return SemVersion.parse(lines.get(0));
            }
        }
        return null;
    }


    /**
     * Determines the version and stores it in the {@linkplain #getTargetFile() target file}.
     * @throws IOException for I/O errors
     */
    @TaskAction
    public void determineVersion() throws IOException {
        SemVersion version = doDetermineVersion();
        writeVersionToOutputFile(version);
    }


    @Nonnull
    private SemVersion doDetermineVersion() {

        RulesContainerInternal rules = (RulesContainerInternal) getRules();
        if (rules == null) {
            GitVersionExtension gitVersion = getProject().getExtensions().getByType(GitVersionExtension.class);
            rules = (RulesContainerInternal) gitVersion.getRules();
        }

        File gitDir = getGitDirectory();
        if (gitDir == null) {
            return rules.getBaseVersion().toImmutable();
        }

        // Obtain the GitRepositoryFactory from the plugin. (This should better be done by some
        // dependency injection mechanism, but Gradle currently does not allow us to register custom
        // objects into its ServiceRegistry)
        GitVersionPlugin plugin = getProject().getPlugins().findPlugin(GitVersionPlugin.class);
        GitRepositoryFactory gitRepositoryFactory = plugin.getGitRepositoryFactory();

        try (CloseableGitRepository gitRepository = gitRepositoryFactory.getRepository(gitDir)) {

            /* If the branch should be overridden, decorate the GitRepository */
            GitRepository actualGitRepository;
            if (getOverrideBranchName() != null) {
                actualGitRepository = new GitRepositoryWithBranchName(gitRepository, getOverrideBranchName());
            } else {
                actualGitRepository = gitRepository;
            }

            VersioningRules versioningRules = rules.getVersioningRules();
            return versioningRules.evaluate(getProject(), actualGitRepository);
        }
    }


    private void writeVersionToOutputFile(SemVersion version) throws IOException {

        File targetDirectory = getTargetFile().getParentFile();
        //noinspection ResultOfMethodCallIgnored
        targetDirectory.mkdirs();

        try (Writer writer = new FileWriter(getTargetFile())) {
            writer.write(version.toString());
        }
    }
}
