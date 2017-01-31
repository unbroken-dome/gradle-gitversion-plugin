package org.unbrokendome.gradle.plugins.gitversion.version;

public interface MutableSemVersion extends SemVersion {

    MutableSemVersion setMajor(int major);


    MutableSemVersion addMajor(int delta);


    default MutableSemVersion incrementMajor() {
        return addMajor(1);
    }


    MutableSemVersion setMinor(int minor);


    MutableSemVersion addMinor(int delta);


    default MutableSemVersion incrementMinor() {
        return addMinor(1);
    }


    MutableSemVersion setPatch(int patch);


    MutableSemVersion addPatch(int delta);


    default MutableSemVersion incrementPatch() {
        return addPatch(1);
    }


    MutableSemVersion setPrereleaseTag(String prereleaseTag);


    MutableSemVersion setBuildMetadata(String buildMetadata);


    MutableSemVersion set(int major, int minor, int patch, String prereleaseTag, String buildMetadata);


    default MutableSemVersion set(int major, int minor, int patch, String prereleaseTag) {
        return set(major, minor, patch, prereleaseTag, null);
    }


    default MutableSemVersion set(int major, int minor, int patch) {
        return set(major, minor, patch, null, null);
    }


    default MutableSemVersion setFrom(SemVersion version) {
        return set(version.getMajor(), version.getMinor(), version.getPatch(),
                version.getPrereleaseTag(), version.getBuildMetadata());
    }


    default SemVersion toImmutable() {
        return new ImmutableSemVersionImpl(
                getMajor(), getMinor(), getPatch(), getPrereleaseTag(), getBuildMetadata());
    }
}
