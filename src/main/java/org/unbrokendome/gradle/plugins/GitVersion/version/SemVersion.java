package org.unbrokendome.gradle.plugins.gitversion.version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SemVersion {

    int getMajor();

    int getMinor();

    int getPatch();

    @Nullable
    String getPrereleaseTag();

    @Nullable
    String getBuildMetadata();


    @Nonnull
    default MutableSemVersion cloneAsMutable() {
        return new MutableSemVersionImpl()
                .setFrom(this);
    }


    @Nonnull
    static SemVersion create(int major, int minor, int patch,
                             @Nullable String prereleaseTag,
                             @Nullable String buildMetadata) {
        return new ImmutableSemVersionImpl(major, minor, patch, prereleaseTag, buildMetadata);
    }


    @Nonnull
    static SemVersion create(int major, int minor, int patch, String prereleaseTag) {
        return create(major, minor, patch, prereleaseTag, null);
    }


    @Nonnull
    static SemVersion create(int major, int minor, int patch) {
        return create(major, minor, patch, null, null);
    }


    @Nonnull
    static SemVersion immutableCopyOf(SemVersion version) {
        if (version instanceof ImmutableSemVersionImpl) {
            return version;

        } else {
            return create(version.getMajor(), version.getMinor(), version.getPatch(),
                    version.getPrereleaseTag(), version.getBuildMetadata());
        }
    }


    @Nonnull
    static SemVersion parse(String input) {
        return SemVersionParser.parse(input);
    }
}
