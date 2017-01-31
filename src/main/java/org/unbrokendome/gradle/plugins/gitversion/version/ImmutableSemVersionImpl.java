package org.unbrokendome.gradle.plugins.gitversion.version;

import java.util.Objects;

import javax.annotation.Nullable;


public final class ImmutableSemVersionImpl implements SemVersion {

    private final int major;
    private final int minor;
    private final int patch;
    @Nullable private final String prereleaseTag;
    @Nullable private final String buildMetadata;


    ImmutableSemVersionImpl(int major, int minor, int patch,
                                   @Nullable String prereleaseTag,
                                   @Nullable String buildMetadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prereleaseTag = prereleaseTag;
        this.buildMetadata = buildMetadata;
    }


    @Override
    public int getMajor() {
        return major;
    }


    @Override
    public int getMinor() {
        return minor;
    }


    @Override
    public int getPatch() {
        return patch;
    }


    @Override
    @Nullable
    public String getPrereleaseTag() {
        return prereleaseTag;
    }


    @Override
    @Nullable
    public String getBuildMetadata() {
        return buildMetadata;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append(major)
                .append('.').append(minor)
                .append('.').append(patch);
        if (prereleaseTag != null && !prereleaseTag.isEmpty()) {
            builder.append('-').append(prereleaseTag);
        }
        if (buildMetadata != null && !buildMetadata.isEmpty()) {
            builder.append('+').append(buildMetadata);
        }
        return builder.toString();
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ImmutableSemVersionImpl) && equals((ImmutableSemVersionImpl) obj);
    }


    private boolean equals(ImmutableSemVersionImpl other) {
        return this.major == other.major
                && this.minor == other.minor
                && this.patch == other.patch
                && Objects.equals(this.prereleaseTag, other.prereleaseTag)
                && Objects.equals(this.buildMetadata, other.buildMetadata);
    }


    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, prereleaseTag, buildMetadata);
    }
}
