package org.unbrokendome.gradle.plugins.gitversion

import org.eclipse.jgit.lib.ObjectId

class GitCommit implements HasObjectId {

    final ObjectId objectId

    GitCommit(ObjectId objectId) {
        this.objectId = objectId
    }

    String getId() { objectId.name() }

    String id(int length) {
        return objectId.abbreviate(length).name()
    }
}

