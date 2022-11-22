package com.coditory.quark.i18n.loader;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

record PathTemplate(String original, Pattern filePattern, String baseDirectory) {
    private static final Set<Integer> WHITELIST_CHARS = (
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    "/-_.*"
    )
            .chars()
            .boxed()
            .collect(toSet());
    private static final String PREFIXES_GROUP_MARKER = "{prefixes}";
    private static final String PREFIX_GROUP_MARKER = "{prefix}";
    private static final String LOCALE_GROUP_MARKER = "{locale}";
    private static final String LANG_GROUP_MARKER = "{lang}";
    private static final String COUNTRY_GROUP_MARKER = "{country}";

    public static PathTemplate of(String path) {
        validate(path);
        String patternString = SimpleFilePattern.compile(path)
                .pattern()
                .replace(PREFIXES_GROUP_MARKER, "\\E(?<prefixes>[a-zA-Z0-9/-_.]+)\\Q")
                .replace(PREFIX_GROUP_MARKER, "\\E(?<prefix>[a-zA-Z0-9-_.]+)\\Q")
                .replace(LANG_GROUP_MARKER, "\\E(?<lang>[a-z]+)\\Q")
                .replace(COUNTRY_GROUP_MARKER, "\\E(?<country>[A-Z]+)\\Q")
                .replace(LOCALE_GROUP_MARKER, "\\E(?<lang>[a-z]+)(_(?<country>[A-Z]+)?)\\Q");
        Pattern pattern = Pattern.compile(patternString);
        String directory = SimpleFilePattern.extractBaseDir(path);
        return new PathTemplate(path, pattern, directory);
    }

    public Locale extractLocale(String path) {
        if (!original.contains(LANG_GROUP_MARKER) || !original.contains(LOCALE_GROUP_MARKER)) {
            return null;
        }
        Matcher matcher = filePattern.matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        String langMatch = matcher.group("lang");
        if (langMatch == null) {
            return null;
        }
        String countryMatch = matcher.group("country");
        String tag = langMatch;
        if (countryMatch != null) {
            tag = langMatch + "_" + countryMatch;
        }
        return Locale.forLanguageTag(tag);
    }

    public String extractPrefix(String path) {
        if (!original.contains(PREFIXES_GROUP_MARKER) || !original.contains(PREFIX_GROUP_MARKER)) {
            return null;
        }
        Matcher matcher = filePattern.matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        String prefixMatch = matcher.group("prefixes");
        prefixMatch = prefixMatch == null ? matcher.group("prefix") : prefixMatch;
        if (prefixMatch == null) {
            return null;
        }
        return prefixMatch
                .replace('/', '.')
                .replace('\\', '.');
    }

    private static void validate(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Expected non-blank path");
        }
        if (path.endsWith("/")) {
            throw new IllegalArgumentException("Expected path to not end with /. Path: " + path);
        }
        if (path.contains("\\")) {
            throw new IllegalArgumentException("Expected unix file separators. Path: " + path);
        }
        if (count(path, PREFIX_GROUP_MARKER) > 1) {
            throw new IllegalArgumentException("Expected at most one " + PREFIX_GROUP_MARKER + " in path: " + path);
        }
        if (count(path, LOCALE_GROUP_MARKER) > 1) {
            throw new IllegalArgumentException("Expected at most one " + LOCALE_GROUP_MARKER + " in path: " + path);
        }
        if (count(path, LANG_GROUP_MARKER) > 1) {
            throw new IllegalArgumentException("Expected at most one " + LANG_GROUP_MARKER + " in path: " + path);
        }
        if (count(path, COUNTRY_GROUP_MARKER) > 1) {
            throw new IllegalArgumentException("Expected at most one " + COUNTRY_GROUP_MARKER + " in path: " + path);
        }
        if (count(path, LOCALE_GROUP_MARKER) > 0 && (count(path, LANG_GROUP_MARKER) + count(path, COUNTRY_GROUP_MARKER) > 0)) {
            throw new IllegalArgumentException("Expected one" + LOCALE_GROUP_MARKER + " or " + LANG_GROUP_MARKER + ", " + COUNTRY_GROUP_MARKER + " in path: " + path);
        }
        if (path.contains("***")) {
            throw new IllegalArgumentException("Too many stars in path: " + path);
        }
        if (path.endsWith("**")) {
            throw new IllegalArgumentException("Path should not end with **. Path: " + path);
        }
        if (count(path, "**") != count(path, "**/")) {
            throw new IllegalArgumentException("Dir placeholder (**) should be followed by '/'. Path: " + path);
        }
        for (int c : path.toCharArray()) {
            if (!WHITELIST_CHARS.contains(c)) {
                throw new IllegalArgumentException("Invalid character in path template: '" + (char) c + "'. Path: " + path);
            }
        }
    }

    private static int count(String text, String substring) {
        int result = 0;
        int index = text.indexOf(substring);
        while (index > 0) {
            index = text.indexOf(substring, index + substring.length());
            result++;
        }
        return result;
    }
}
