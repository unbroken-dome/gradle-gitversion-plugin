package org.unbrokendome.gradle.plugins.gitversion.version;

public interface SemVersion {

    int getMajor();

    int getMinor();

    int getPatch();

    String getPrereleaseTag();

    String getBuildMetadata();


    default MutableSemVersion cloneAsMutable() {
        return new MutableSemVersionImpl()
                .setFrom(this);
    }


    static SemVersion create(int major, int minor, int patch, String prereleaseTag, String buildMetadata) {
        return new ImmutableSemVersionImpl(major, minor, patch, prereleaseTag, buildMetadata);
    }


    static SemVersion create(int major, int minor, int patch, String prereleaseTag) {
        return create(major, minor, patch, prereleaseTag, null);
    }


    static SemVersion create(int major, int minor, int patch) {
        return create(major, minor, patch, null, null);
    }


    static SemVersion immutableCopyOf(SemVersion version) {
        if (version instanceof ImmutableSemVersionImpl) {
            return version;

        } else {
            return create(version.getMajor(), version.getMinor(), version.getPatch(),
                    version.getPrereleaseTag(), version.getBuildMetadata());
        }
    }


    static SemVersion parse(String input) {
        return SemVersionParser.parse(input);
    }
}
