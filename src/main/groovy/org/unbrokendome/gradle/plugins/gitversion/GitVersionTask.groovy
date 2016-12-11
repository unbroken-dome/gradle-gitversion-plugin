package org.unbrokendome.gradle.plugins.gitversion

import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject
import java.util.regex.Pattern


class GitVersionTask extends AbstractTask {

    private final List<BranchRule> branchRules = []
    private Closure<?> detachedHeadRule = null

    @Inject
    GitVersionTask() {
        this.repositoryLocation = project.file('.git')
    }

    @InputDirectory
    File repositoryLocation


    void onBranch(Pattern branchNamePattern,
                  @DelegatesTo(GitContext) Closure<?> configClosure) {
        branchRules << new BranchRule(branchNamePattern, configClosure)
    }


    void onBranch(String branchName,
                  @DelegatesTo(GitContext) Closure<?> configClosure) {
        def branchNamePattern = Pattern.compile(Pattern.quote(branchName))
        onBranch(branchNamePattern, configClosure)
    }


    void onDetachedHead(@DelegatesTo(GitContext) Closure<?> configClosure) {
        detachedHeadRule = configClosure
    }


    @TaskAction
    void determineVersion() {
        def repository = new FileRepositoryBuilder().setGitDir(repositoryLocation).build()

        def branchName = getBranchName(repository)

        if (branchName != null) {
            branchRules.each { branchRule ->
                def matcher = branchRule.branchNamePattern.matcher(branchName)
                if (matcher.matches()) {
                    def configClosure = branchRule.configClosure.clone() as Closure<?>
                    configClosure.delegate = new GitContext(project, repository, matcher)
                    configClosure.call()
                }
            }

        } else {
            if (detachedHeadRule != null) {
                def configClosure = detachedHeadRule.clone() as Closure<?>
                configClosure.delegate = new GitContext(project, repository)
                configClosure.call()
            }
        }
    }


    private static String getBranchName(Repository repository) {
        def branchFromEnvironment = System.getenv('BRANCH_NAME')
        if (branchFromEnvironment != null) {
            return branchFromEnvironment
        }

        def head = repository.exactRef(Constants.HEAD)
        return head.isSymbolic() ? head.target.name : null
    }
}
