package org.unbrokendome.gradle.plugins.gitversion.util;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nullable;

public final class RepositoryUtils {

    private RepositoryUtils() { }


    @Nullable
    public static Path findGitDirectory(Path startPath) {
        if (".git".equals(startPath.getFileName().toString())) {
            return startPath;
        }

        Path path = startPath;
        while (path != null) {
            Path gitDir = path.resolve(".git");
            if (Files.exists(gitDir)) {
                return gitDir;
            }
            path = path.getParent();
        }

        return null;
    }


    @Nullable
    public static File findGitDirectory(File startDir) {
        Path gitPath = findGitDirectory(startDir.toPath());
        return gitPath != null ? gitPath.toFile() : null;
    }
}
