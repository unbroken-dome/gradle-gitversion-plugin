package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;


public interface HasObjectId {

    @Nonnull
    String getId();

    @Nonnull
    default String id(int length) {
        return getId().substring(0, length);
    }
}
