package org.unbrokendome.gradle.plugins.gitversion

import org.eclipse.jgit.lib.ObjectId

interface HasObjectId {

    ObjectId getObjectId()
}
