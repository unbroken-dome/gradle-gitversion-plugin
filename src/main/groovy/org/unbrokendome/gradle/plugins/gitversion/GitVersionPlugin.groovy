package org.unbrokendome.gradle.plugins.gitversion

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject


class GitVersionPlugin implements Plugin<Project> {

    private final Instantiator instantiator

    @Inject
    GitVersionPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        project.tasks.create('gitVersion', GitVersionTask)
    }
}
