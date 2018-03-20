package org.unbrokendome.gradle.plugins.gitversion.internal;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unbrokendome.gradle.plugins.gitversion.core.BranchPoint;
import org.unbrokendome.gradle.plugins.gitversion.core.MatcherFacade;
import org.unbrokendome.gradle.plugins.gitversion.core.TaggedCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.CloseableIterator;
import org.unbrokendome.gradle.plugins.gitversion.model.GitBranch;
import org.unbrokendome.gradle.plugins.gitversion.model.GitCommit;
import org.unbrokendome.gradle.plugins.gitversion.model.GitRepository;
import org.unbrokendome.gradle.plugins.gitversion.model.GitTag;
import org.unbrokendome.gradle.plugins.gitversion.model.HasObjectId;
import org.unbrokendome.gradle.plugins.gitversion.util.collections.CollectionUtils;

import groovy.lang.Tuple2;


public class GitOperations {

    private final GitRepository repository;


    public GitOperations(GitRepository repository) {
        this.repository = repository;
    }


    @Nullable
    public BranchPoint branchPoint(String... otherBranchNames) {

        Stream<GitBranch> otherBranches = Arrays.stream(otherBranchNames)
                .map(repository::getBranch)
                .filter(Objects::nonNull);

        return branchPoint(otherBranches);
    }


    @Nullable
    public BranchPoint branchPoint(Pattern otherBranchNamePattern) {

        Stream<GitBranchWithMatcher> otherBranches = findMatchingBranchNames(otherBranchNamePattern);

        BranchPoint branchPoint = branchPoint(otherBranches);

        if (branchPoint != null) {
            GitBranchWithMatcher otherBranch = (GitBranchWithMatcher) branchPoint.getOtherBranch();
            MatcherFacade matches = new DefaultMatcherFacade(otherBranch.getMatcher());
            return new BranchPointImpl(branchPoint.getCommit(), otherBranch.getBranch(), matches);

        } else {
            return null;
        }
    }


    @Nullable
    private BranchPoint branchPoint(Stream<? extends GitBranch> otherBranches) {

        try (CloseableIterator<GitCommit> commitsOnThisBranch = repository.firstParentWalk()) {

            Map<GitBranch, CloseableIterator<GitCommit>> commitsOnOtherBranches = otherBranches
                    .collect(Collectors.toMap(
                            Function.identity(),
                            branch -> repository.firstParentWalk(branch.getHead())));

            try {
                Tuple2<GitBranch, GitCommit> commonCommit =
                        CollectionUtils.findFirstCommonItem(commitsOnThisBranch, commitsOnOtherBranches);
                if (commonCommit != null) {
                    return new BranchPointImpl(commonCommit.getSecond(), commonCommit.getFirst());
                } else {
                    return null;
                }

            } finally {
                commitsOnOtherBranches.values().forEach(CloseableIterator::close);
            }
        }
    }


    @Nonnull
    private Stream<GitBranchWithMatcher> findMatchingBranchNames(Pattern branchNamePattern) {
        return repository.getBranches().stream()
                .map(branch -> {
                    Matcher matcher = branchNamePattern.matcher(branch.getFullName());
                    if (!matcher.matches()) {
                        matcher = branchNamePattern.matcher(branch.getShortName());
                    }
                    if (matcher.matches()) {
                        return new GitBranchWithMatcher(branch, matcher);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }


    public int countCommitsSince(HasObjectId obj, boolean countMergeCommits) {
        GitCommit head = repository.getHead();
        if (head != null) {
            ReachableCommitSet commitSet = new ReachableCommitSet(head, countMergeCommits);
            do {
                if (commitSet.contains(obj)) {
                    return commitSet.size();
                }
            } while (commitSet.grow());
        }
        return -1;
    }


    private static class ReachableCommitSet {

        private final Set<String> visitedIds = new HashSet<>();
        private Set<GitCommit> nextStepCommits = Collections.emptySet();
        private final boolean countMergeCommits;
        private int size = 0;

        ReachableCommitSet(GitCommit startCommit, boolean countMergeCommits) {
            this.countMergeCommits = countMergeCommits;
            addCommits(Collections.singleton(startCommit));
        }

        private boolean addCommits(Collection<GitCommit> commits) {
            nextStepCommits = new HashSet<>();
            Queue<GitCommit> commitsToAdd = new ArrayDeque<>(commits);
            GitCommit nextCommitToAdd;
            boolean anyAdded = false;
            while ((nextCommitToAdd = commitsToAdd.poll()) != null) {
                if (visitedIds.add(nextCommitToAdd.getId())) {
                    anyAdded = true;
                    List<? extends GitCommit> parents = nextCommitToAdd.getParents();
                    if (!countMergeCommits && parents.size() > 1) {
                        commitsToAdd.addAll(parents);
                    } else {
                        nextStepCommits.addAll(parents);
                    }
                }
            }
            return anyAdded;
        }

        boolean grow() {
            if (addCommits(nextStepCommits)) {
                size++;
                return true;
            } else {
                return false;
            }
        }

        boolean contains(HasObjectId obj) {
            return visitedIds.contains(obj.getId());
        }

        int size() {
            return size;
        }
    }


    @Nullable
    public TaggedCommit findLatestTag(Pattern tagNamePattern, boolean includeMerges) {
        GitRepository.WalkMode walkMode = includeMerges ?
                GitRepository.WalkMode.ALL : GitRepository.WalkMode.FIRST_PARENT_ONLY;

        try (CloseableIterator<GitCommit> commits = repository.walk(walkMode)) {
            while (commits.hasNext()) {
                GitCommit commit = commits.next();
                for (GitTag tag : commit.getTags()) {
                    Matcher matcher = tagNamePattern.matcher(tag.getName());
                    if (matcher.matches()) {
                        return new TaggedCommitImpl(tag, new DefaultMatcherFacade(matcher));
                    }
                }
            }
        }

        return null;
    }


    private static final class GitBranchWithMatcher implements GitBranch {

        private final GitBranch branch;
        private final Matcher matcher;


        private GitBranchWithMatcher(GitBranch branch, Matcher matcher) {
            this.branch = branch;
            this.matcher = matcher;
        }


        public GitBranch getBranch() {
            return branch;
        }

        Matcher getMatcher() {
            return matcher;
        }

        @Override
        @Nonnull
        public String getShortName() {
            return branch.getShortName();
        }


        @Override
        @Nonnull
        public String getFullName() {
            return branch.getFullName();
        }


        @Override
        @Nonnull
        public GitCommit getHead() {
            return branch.getHead();
        }
    }
}
