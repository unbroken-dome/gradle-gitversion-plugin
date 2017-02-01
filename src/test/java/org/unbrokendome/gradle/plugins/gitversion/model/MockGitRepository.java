package org.unbrokendome.gradle.plugins.gitversion.model;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class MockGitRepository implements CloseableGitRepository {

    private final File workingDir;
    private final Map<String, ? extends GitBranch> branches;
    private final Map<String, ? extends GitTag> tags;
    private final String currentBranchName;
    private final GitCommit head;

    MockGitRepository(File workingDir,
                      Map<String, ? extends GitBranch> branches,
                      Map<String, ? extends GitTag> tags,
                      @Nullable String currentBranchName,
                      @Nullable GitCommit head) {
        this.workingDir = workingDir;
        this.branches = branches;
        this.tags = tags;
        this.currentBranchName = currentBranchName;
        this.head = head;
    }


    @Override
    public File getWorkingDir() {
        return workingDir;
    }


    @Nullable
    @Override
    public GitCommit getHead() {
        return head;
    }


    @Nullable
    @Override
    public GitBranch getCurrentBranch() {
        return branches.get(currentBranchName);
    }


    @Nullable
    @Override
    public GitBranch getBranch(String name) {
        String fullName = name;
        if (!name.startsWith("refs/heads/") && !name.startsWith("refs/remotes/")) {
            String[] candidateFullNames = { "refs/heads/" + name, "refs/remotes/" + name };
            for (String candidateFullName : candidateFullNames) {
                if (branches.containsKey(candidateFullName)) {
                    fullName = candidateFullName;
                    break;
                }
            }
        }
        return branches.get(fullName);
    }


    @Nonnull
    @Override
    public Collection<? extends GitBranch> getBranches() {
        return branches.values();
    }


    @Nonnull
    @Override
    public Collection<? extends GitTag> getTags() {
        return tags.values();
    }


    @Nonnull
    @Override
    public Set<String> getRemoteNames() {
        return Collections.singleton("origin");
    }


    @Nonnull
    @Override
    public CloseableIterator<GitCommit> walk(GitCommit startCommit, WalkMode mode) {

        Stack<GitCommit> commitsToVisit = new Stack<>();
        Set<GitCommit> commitsVisited = new HashSet<>();
        List<GitCommit> result = new ArrayList<>();

        Function<GitCommit, List<? extends GitCommit>> getParentsFunc;
        switch (mode) {
            case ALL:
                getParentsFunc = GitCommit::getParents;
                break;
            case FIRST_PARENT_ONLY:
                getParentsFunc = commit -> commit.getParents().stream()
                        .limit(1).collect(Collectors.toList());
                break;
            default:
                getParentsFunc = commit -> Collections.emptyList();
                break;
        }

        commitsToVisit.add(startCommit);

        while (!commitsToVisit.isEmpty()) {
            GitCommit commit = commitsToVisit.pop();

            List<? extends GitCommit> unseenParents = getParentsFunc.apply(commit).stream()
                    .filter(parent -> !commitsVisited.contains(parent))
                    .collect(Collectors.toList());

            if (!unseenParents.isEmpty()) {
                commitsToVisit.push(commit);
                commitsToVisit.addAll(unseenParents);
            } else {
                commitsVisited.add(commit);
                result.add(commit);
            }
        }

        return new CloseableIteratorImpl<>(Lists.reverse(result).iterator());
    }


    @Override
    public void close() {
    }


    public static MockGitRepositoryBuilder builder() {
        return new MockGitRepositoryBuilder();
    }
}
