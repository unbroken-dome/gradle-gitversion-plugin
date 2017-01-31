package org.unbrokendome.gradle.plugins.gitversion.model.jgit;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableGitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableIterator;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.util.Lazy;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOBiFunction;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOConsumer;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOFunction;
import org.unbrokendome.gradle.plugins.gitversion.util.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public final class JGitRepository implements CloseableGitRepository {

    private final Repository repository;
    private final Supplier<Multimap<ObjectId, JGitTag>> tagsByCommitId = Lazy.of(this::retrieveTagsByCommitId);


    JGitRepository(Repository repository) {
        this.repository = repository;
    }


    Repository getRepository() {
        return repository;
    }


    @Override
    public File getWorkingDir() {
        return repository.getWorkTree();
    }


    @Nullable
    @Override
    public GitCommit getHead() {
        return IOUtils.unchecked(() -> {
            Ref head = repository.exactRef(Constants.HEAD);
            if (head == null) {
                return null;
            }
            ObjectId objectId = head.getLeaf().getObjectId();
            return new JGitCommit(this, objectId);
        });
    }


    @Nullable
    @Override
    public GitBranch getCurrentBranch() {
        return IOUtils.unchecked(() -> {
            Ref head = repository.exactRef(Constants.HEAD);
            if (head == null) {
                return null;
            }

            while (head.isSymbolic()) {
                head = head.getTarget();
            }

            return new JGitBranch(this, head);
        });
    }


    @Nonnull
    @Override
    public Collection<? extends GitBranch> getBranches() {
        return repository.getAllRefs().values().stream()
                .filter(ref -> {
                    String refName = ref.getName();
                    return refName.startsWith(Constants.R_HEADS) || refName.startsWith(Constants.R_REMOTES);
                })
                .map(ref -> new JGitBranch(this, ref))
                .collect(Collectors.toList());
    }


    @Nullable
    @Override
    public GitBranch getBranch(String name) {
        return IOUtils.unchecked(() -> {
            Ref ref = getBranchRef(name);
            return ref != null ? new JGitBranch(this, ref) : null;
        });
    }


    private Ref getBranchRef(String name) throws IOException {
        if (name.startsWith(Constants.R_REFS)) {
            return repository.exactRef(name);
        } else if (name.startsWith("heads/") || name.startsWith("remotes/")) {
            return repository.exactRef(Constants.R_REFS + name);
        } else {
            Ref ref = repository.findRef(name);
            if (ref != null) {
                String resolvedName = ref.getName();
                if (!resolvedName.startsWith(Constants.R_HEADS) && !resolvedName.startsWith(Constants.R_REMOTES)) {
                    // ref was resolved by findRef but it's not a branch (might be a tag), so return null
                    return null;
                }
            }
            return ref;
        }
    }


    @Nonnull
    @Override
    public Collection<JGitTag> getTags() {
        return repository.getTags().entrySet().stream()
                .map(tagEntry -> new JGitTag(this, tagEntry.getKey(), tagEntry.getValue()))
                .collect(Collectors.toList());
    }


    @Nonnull
    @Override
    public CloseableIterator<GitCommit> walk(GitCommit startCommit, WalkMode mode) {

        ObjectId startCommitId = ObjectId.fromString(startCommit.getId());

        IOConsumer<RevWalk> revWalkInitializer = revWalk -> {
            RevCommit start = revWalk.lookupCommit(startCommitId);
            revWalk.markStart(start);
        };

        IOFunction<RevWalk, RevCommit> firstCommitFunction = RevWalk::next;
        IOBiFunction<RevWalk, RevCommit, RevCommit> nextCommitFunction = (revWalk, commit) -> revWalk.next();

        switch (mode) {
            case ALL:
                revWalkInitializer = revWalkInitializer.andThen(revWalk -> {
                    revWalk.sort(RevSort.TOPO, true);
                    revWalk.sort(RevSort.COMMIT_TIME_DESC, true);
                });
                break;

            case FIRST_PARENT_ONLY:
                firstCommitFunction = revWalk -> revWalk.parseCommit(startCommitId);
                nextCommitFunction = (revWalk, commit) -> {
                    if (commit.getParentCount() > 0) {
                        return revWalk.parseCommit(commit.getParent(0));
                    } else {
                        return null;
                    }
                };
                break;
        }

        return new JGitWalkIterator(this, revWalkInitializer, firstCommitFunction, nextCommitFunction);
    }


    @Override
    public void close() {
        repository.close();
    }


    Multimap<ObjectId, JGitTag> getTagsByCommitId() {
        return tagsByCommitId.get();
    }


    private Multimap<ObjectId, JGitTag> retrieveTagsByCommitId() {
        return Multimaps.index(getTags(), JGitTag::getTargetObjectId);
    }
}
