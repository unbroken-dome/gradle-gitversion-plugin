package org.unbrokendome.gradle.plugins.gitversion.version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MutableSemVersion extends SemVersion {

    @Nonnull
    MutableSemVersion setMajor(int major);


    @Nonnull
    MutableSemVersion addMajor(int delta);


    @Nonnull
    default MutableSemVersion incrementMajor() {
        return addMajor(1);
    }


    @Nonnull
    MutableSemVersion setMinor(int minor);


    @Nonnull
    MutableSemVersion addMinor(int delta);


    @Nonnull
    default MutableSemVersion incrementMinor() {
        return addMinor(1);
    }


    @Nonnull
    MutableSemVersion setPatch(int patch);


    @Nonnull
    MutableSemVersion addPatch(int delta);


    @Nonnull
    default MutableSemVersion incrementPatch() {
        return addPatch(1);
    }


    @Nonnull
    MutableSemVersion setPrereleaseTag(String prereleaseTag);


    @Nonnull
    MutableSemVersion setBuildMetadata(String buildMetadata);


    @Nonnull
    MutableSemVersion set(int major, int minor, int patch,
                          @Nullable String prereleaseTag,
                          @Nullable String buildMetadata);


    @Nonnull
    default MutableSemVersion set(int major, int minor, int patch,
                                  @Nullable String prereleaseTag) {
        return set(major, minor, patch, prereleaseTag, null);
    }


    @Nonnull
    default MutableSemVersion set(int major, int minor, int patch) {
        return set(major, minor, patch, null, null);
    }


    @Nonnull
    default MutableSemVersion setFrom(SemVersion version) {
        return set(version.getMajor(), version.getMinor(), version.getPatch(),
                version.getPrereleaseTag(), version.getBuildMetadata());
    }


    @Nonnull
    default MutableSemVersion setFrom(String versionString) {
        return setFrom(SemVersion.parse(versionString));
    }


    @Nonnull
    default SemVersion toImmutable() {
        return new ImmutableSemVersionImpl(
                getMajor(), getMinor(), getPatch(), getPrereleaseTag(), getBuildMetadata());
    }
}
