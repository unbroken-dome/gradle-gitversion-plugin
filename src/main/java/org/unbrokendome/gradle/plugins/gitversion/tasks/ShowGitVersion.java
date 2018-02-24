package org.unbrokendome.gradle.plugins.gitversion.tasks;

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.unbrokendome.gradle.plugins.gitversion.version.SemVersion;

import java.io.IOException;
import java.util.Collections;


/**
 * A task that prints the version on standard out.
 *
 * <p>This task reads the version from the file that was
 * written earlier by a {@link DetermineGitVersion} task.
 */
@SuppressWarnings("WeakerAccess")
public class ShowGitVersion extends ConventionTask {

    private DetermineGitVersion fromTask;


    @Internal
    public DetermineGitVersion getFromTask() {
        return fromTask;
    }


    public void setFromTask(DetermineGitVersion fromTask) {
        this.fromTask = fromTask;
        setDependsOn(Collections.singleton(fromTask));
    }


    public void from(DetermineGitVersion fromTask) {
        setFromTask(fromTask);
    }


    @TaskAction
    public void showGitVersion() throws IOException {
        if (fromTask != null) {
            SemVersion cachedVersion = fromTask.getCachedVersion();
            if (cachedVersion != null) {
                System.out.println(cachedVersion);
            }
        }
    }
}
