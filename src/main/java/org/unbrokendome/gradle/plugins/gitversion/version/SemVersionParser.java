package org.unbrokendome.gradle.plugins.gitversion.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class SemVersionParser {

    private static final Pattern PATTERN = Pattern.compile(
            "(?<major>[1-9][0-9]*)"
                    + "(\\.(?<minor>[1-9][0-9]*)"
                    + "(\\.(?<patch>[1-9][0-9]*))?)?"
                    + "(-(?<prerelease>[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*))?"
                    + "(\\+(?<buildmeta>[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*))?");


    @Nonnull
    public static SemVersion parse(@Nullable String input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null");
        }

        Matcher matcher = PATTERN.matcher(input.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid semver: \"" + input + "\"");
        }

        int major = Integer.parseInt(matcher.group("major"));
        int minor = parseIntOrZero(matcher.group("minor"));
        int patch = parseIntOrZero(matcher.group("patch"));
        String prereleaseTag = matcher.group("prerelease");
        String buildMetadata = matcher.group("buildmeta");
        return new ImmutableSemVersionImpl(major, minor, patch, prereleaseTag, buildMetadata);
    }


    private static int parseIntOrZero(@Nullable String input) {
        return input != null ? Integer.parseInt(input) : 0;
    }
}
