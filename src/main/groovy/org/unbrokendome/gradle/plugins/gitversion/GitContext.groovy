package org.unbrokendome.gradle.plugins.gitversion

import com.github.zafarkhaja.semver.Version
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.gradle.api.Project

import javax.annotation.RegEx
import java.util.function.UnaryOperator
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Stream

class GitContext {

    final Project project
    private final Repository repository
    private final Matcher matcher

    GitContext(Project project, Repository repository, Matcher matcher = null) {
        this.project = project
        this.repository = repository
        this.matcher = matcher
    }


    String getBranchName() {
        def branchFromEnvironment = System.getenv('BRANCH_NAME')
        if (branchFromEnvironment != null) {
            return branchFromEnvironment
        }

        def head = repository.exactRef(Constants.HEAD)
        return head.isSymbolic() ? head.target.name : null
    }


    GitCommit getHead() {
        new GitCommit(repository.resolve('HEAD'))
    }


    String getCommitId() {
        head.id
    }


    Version semver(String versionString, String defaultVersionString = null) {
        if (versionString != null) {
            Version.valueOf(versionString)
        } else if (defaultVersionString != null) {
            Version.valueOf(defaultVersionString)
        } else {
            null
        }
    }


    Version semver(int major) { Version.forIntegers(major) }

    Version semver(int major, int minor) { Version.forIntegers(major, minor) }

    Version semver(int major, int minor, int patch) { Version.forIntegers(major, minor, patch) }


    MatcherFacade getMatches() {
        return (matcher != null) ? new DefaultMatcherFacade(matcher) : NullMatcherFacade.INSTANCE
    }


    BranchPoint branchPoint(String other = 'origin/master', String ref = repository.branch) {
        def commonCommit = CollectionUtils.findFirstCommonItem(
                firstParentWalk(ref),
                firstParentWalk(other))
        return commonCommit?.with { new BranchPoint(other, commonCommit) }
    }


    BranchPoint firstBranchPoint(Pattern otherPattern, String ref = repository.branch) {
        def branches = new Git(repository).branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .findAll { otherPattern.matcher(it.name).matches() }
                .collectEntries { [it.name, firstParentWalk(it)] }

        Tuple2<String, RevCommit> result = CollectionUtils.findFirstCommonItem(firstParentWalk(ref), branches)
        result?.with {
            def matcher = otherPattern.matcher(it.first)
            matcher.matches()
            new BranchPoint(it.first, it.second, matcher)
        }
    }


    BranchPoint firstBranchPoint(@RegEx String otherPattern,
                                 String ref = repository.branch) {
        firstBranchPoint(Pattern.compile(otherPattern), ref)
    }


    int countCommitsSince(ObjectId objectId) {
        firstParentWalk('HEAD')
                .findIndexOf { it.toObjectId() == objectId }
    }


    int countCommitsSince(HasObjectId commit) {
        if (commit == null) {
            return 0
        }
        countCommitsSince(commit.objectId)
    }


    private Iterator<RevCommit> firstParentWalk(ObjectId refId) {
        def revWalk = new RevWalk(repository)
        def startCommit = revWalk.parseCommit(refId)

        return Stream.iterate(startCommit,
                (UnaryOperator) { RevCommit c -> firstParent(revWalk, c) })
                .iterator()
                .takeWhile { it != null }
    }


    private Iterator<RevCommit> firstParentWalk(Ref ref) {
        firstParentWalk(ref.leaf.objectId)
    }


    private Iterator<RevCommit> firstParentWalk(String ref) {
        firstParentWalk(repository.resolve(ref))
    }


    private static RevCommit firstParent(RevWalk revWalk, RevCommit commit) {
        commit.parents?.collect { revWalk.parseCommit(it) }?.find()
    }
}
