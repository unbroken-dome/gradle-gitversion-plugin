package org.unbrokendome.gradle.plugins.gitversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nullable;


public class TestRepository extends ExternalResource {

    private final TemporaryFolder rootDir = new TemporaryFolder();

    /** The directory where the (bare) repository is stored; this will serve as the remote */
    private File bareRepositoryDir;
    /** The working directory where the repository is checked out; this will be available to the test */
    private File workingDir;

    /** The Git object for setting up the repository contents */
    private Git setupGit;
    /** The Git object for the working dir */
    private Git git;

    @Override
    protected void before() throws Throwable {
        rootDir.create();

        bareRepositoryDir = rootDir.newFolder("repo");
        workingDir = rootDir.newFolder("working");

        // initialize the bare repository
        Git.init()
                .setDirectory(bareRepositoryDir)
                .setBare(true)
                .call();

        // clone the repository into the setup working dir
        File setupWorkingDir = rootDir.newFolder("setup");
        setupGit = Git.cloneRepository()
                .setDirectory(setupWorkingDir)
                .setURI(bareRepositoryDir.toURI().toString())
                .call();
    }


    public void setup(ThrowingConsumer<GitBuilder> action) throws Exception {

        GitBuilder builder = new GitBuilder(setupGit);
        action.accept(builder);

        // push all local branches to origin
        List<Ref> localBranches = setupGit.branchList().call();
        PushCommand pushCommand = setupGit.push()
                .setRemote("origin");
        localBranches.forEach(pushCommand::add);
        pushCommand.call();
    }


    public File cloneAndCheckout(String branchName) throws GitAPIException {
        git = Git.cloneRepository()
                .setDirectory(workingDir)
                .setURI(bareRepositoryDir.toURI().toString())
                .setBranch(branchName)
                .call();

        return workingDir;
    }


    public void detach() throws GitAPIException, IOException {
        ObjectId headId = git.getRepository().resolve("HEAD");
        git.checkout()
                .setName(headId.name())
                .call();
    }


    public File getWorkingDir() {
        return workingDir;
    }


    public Git getGit() {
        return git;
    }


    @Override
    protected void after() {
        git.close();
        setupGit.close();
        rootDir.delete();
    }


    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }


    static class GitBuilder {

        private final Git git;
        private final Path workingDir;


        GitBuilder(Git git) {
            this.git = git;
            this.workingDir = git.getRepository().getWorkTree().toPath();
        }


        public GitBuilder commitFile(String fileName, String commitMessage, @Nullable String fileContents)
                throws Exception {

            Path path = workingDir.resolve(fileName);
            if (fileContents != null) {
                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(fileContents);
                }
            }
            git.add()
                    .addFilepattern(fileName)
                    .call();
            git.commit()
                    .setMessage(commitMessage)
                    .call();
            return this;
        }

        public GitBuilder commitFile(String fileName, String commitMessage) throws Exception {
            return commitFile(fileName, commitMessage, null);
        }

        public GitBuilder checkout(String branch) throws Exception {
            git.checkout()
                    .setName(branch)
                    .call();
            return this;
        }

        public GitBuilder checkoutNew(String branch) throws Exception {
            git.checkout()
                    .setName(branch)
                    .setCreateBranch(true)
                    .call();
            return this;
        }

        public GitBuilder merge(String fromBranch, @Nullable String message) throws Exception {
            git.merge()
                    .include(git.getRepository().findRef(fromBranch))
                    .setFastForward(MergeCommand.FastForwardMode.NO_FF)
                    .setCommit(true)
                    .setMessage(message)
                    .call();
            return this;
        }

        public GitBuilder merge(String fromBranch) throws Exception {
            return merge(fromBranch, null);
        }

        public GitBuilder tag(String tagName) throws Exception {
            git.tag()
                    .setName(tagName)
                    .call();
            return this;
        }
    }
}
