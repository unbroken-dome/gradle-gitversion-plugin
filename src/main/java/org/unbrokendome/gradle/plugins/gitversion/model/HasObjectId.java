package org.unbrokendome.gradle.plugins.gitversion.model;

import javax.annotation.Nonnull;


/**
 * Represents any Git object that has an ID in the form of a SHA-256 commit hash.
 */
public interface HasObjectId {

    /**
     * Gets the full SHA-256 commit hash for this object.
     * @return the full commit hash
     */
    @Nonnull
    String getId();

    /**
     * Gets an abbreviated SHA-256 commit hash for this object.
     * @param length the maximum length of the abbreviated ID
     * @return the abbreviated commit hash
     */
    @Nonnull
    default String id(int length) {
        return getId().substring(0, length);
    }
}
