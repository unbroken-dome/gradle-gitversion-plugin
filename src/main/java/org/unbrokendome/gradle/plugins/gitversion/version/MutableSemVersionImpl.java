package org.unbrokendome.gradle.plugins.gitversion.version;

public class MutableSemVersionImpl implements MutableSemVersion {

    private int major = 0;
    private int minor = 1;
    private int patch = 0;
    private String prereleaseTag = null;
    private String buildMetadata = null;


    @Override
    public int getMajor() {
        return major;
    }


    @Override
    public MutableSemVersion setMajor(int major) {
        this.major = major;
        return this;
    }


    @Override
    public MutableSemVersion addMajor(int delta) {
        return setMajor(major + delta);
    }


    @Override
    public int getMinor() {
        return minor;
    }


    @Override
    public MutableSemVersion setMinor(int minor) {
        this.minor = minor;
        return this;
    }


    @Override
    public MutableSemVersion addMinor(int delta) {
        return setMinor(minor + delta);
    }


    @Override
    public int getPatch() {
        return patch;
    }


    @Override
    public MutableSemVersion setPatch(int patch) {
        this.patch = patch;
        return this;
    }


    @Override
    public MutableSemVersion addPatch(int delta) {
        return setPatch(patch + delta);
    }


    @Override
    public String getPrereleaseTag() {
        return prereleaseTag;
    }


    @Override
    public MutableSemVersion setPrereleaseTag(String prereleaseTag) {
        this.prereleaseTag = prereleaseTag;
        return this;
    }


    @Override
    public String getBuildMetadata() {
        return buildMetadata;
    }


    @Override
    public MutableSemVersion setBuildMetadata(String buildMetadata) {
        this.buildMetadata = buildMetadata;
        return this;
    }


    @Override
    public MutableSemVersion set(int major, int minor, int patch, String prereleaseTag, String buildMetadata) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prereleaseTag = prereleaseTag;
        this.buildMetadata = buildMetadata;
        return this;
    }
}
