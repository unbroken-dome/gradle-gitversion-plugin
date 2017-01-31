package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import com.google.common.collect.AbstractIterator;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableIterator;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOBiFunction;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOConsumer;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOFunction;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOUtils;


public class JGitWalkIterator extends AbstractIterator<GitCommit> implements CloseableIterator<GitCommit> {

    private final JGitRepository repository;
    private final IOFunction<RevWalk, RevCommit> firstCommitFunction;
    private final IOBiFunction<RevWalk, RevCommit, RevCommit> nextCommitFunction;
    private final RevWalk revWalk;

    private RevCommit currentCommit = null;


    JGitWalkIterator(JGitRepository repository,
                     IOConsumer<RevWalk> revWalkInitializer,
                     IOFunction<RevWalk, RevCommit> firstCommitFunction,
                     IOBiFunction<RevWalk, RevCommit, RevCommit> nextCommitFunction) {
        this.repository = repository;
        this.firstCommitFunction = firstCommitFunction;
        this.nextCommitFunction = nextCommitFunction;

        this.revWalk = new RevWalk(repository.getRepository());

        IOUtils.unchecked(() -> revWalkInitializer.accept(revWalk));
    }


    @Override
    protected GitCommit computeNext() {

        return IOUtils.unchecked(() -> {

            if (currentCommit == null) {
                currentCommit = firstCommitFunction.apply(revWalk);

            } else {
                currentCommit = nextCommitFunction.apply(revWalk, currentCommit);
            }

            if (currentCommit == null) {
                endOfData();
                return null;
            }

            return new JGitCommit(repository, currentCommit);
        });
    }


    @Override
    public void close() {
        revWalk.close();
    }
}
