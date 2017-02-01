package org.unbrokendome.gradle.plugins.gitversion.version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MutableSemVersionImpl implements MutableSemVersion {

    private int major = 0;
    private int minor = 1;
    private int patch = 0;
    @Nullable private String prereleaseTag = null;
    @Nullable private String buildMetadata = null;


    @Override
    public int getMajor() {
        return major;
    }


    @Nonnull
    @Override
    public MutableSemVersion setMajor(int major) {
        this.major = major;
        return this;
    }


    @Nonnull
    @Override
    public MutableSemVersion addMajor(int delta) {
        return setMajor(major + delta);
    }


    @Override
    public int getMinor() {
        return minor;
    }


    @Nonnull
    @Override
    public MutableSemVersion setMinor(int minor) {
        this.minor = minor;
        return this;
    }


    @Nonnull
    @Override
    public MutableSemVersion addMinor(int delta) {
        return setMinor(minor + delta);
    }


    @Override
    public int getPatch() {
        return patch;
    }


    @Nonnull
    @Override
    public MutableSemVersion setPatch(int patch) {
        this.patch = patch;
        return this;
    }


    @Nonnull
    @Override
    public MutableSemVersion addPatch(int delta) {
        return setPatch(patch + delta);
    }


    @Override
    @Nullable
    public String getPrereleaseTag() {
        return prereleaseTag;
    }


    @Nonnull
    @Override
    public MutableSemVersion setPrereleaseTag(String prereleaseTag) {
        this.prereleaseTag = prereleaseTag;
        return this;
    }


    @Nullable
    @Override
    public String getBuildMetadata() {
        return buildMetadata;
    }


    @Nonnull
    @Override
    public MutableSemVersion setBuildMetadata(String buildMetadata) {
        this.buildMetadata = buildMetadata;
        return this;
    }


    @Nonnull
    @Override
    public MutableSemVersion set(int major, int minor, int patch,
                                 @Nullable String prereleaseTag,
                                 @Nullable String buildMetadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prereleaseTag = prereleaseTag;
        this.buildMetadata = buildMetadata;
        return this;
    }


    @Override
    public String toString() {
        return toImmutable().toString();
    }
}
