package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public final class I18nPath {
    private static final Set<Integer> WHITELIST_CHARS = (
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    "-_"
    )
            .chars()
            .boxed()
            .collect(toSet());
    private static final String SEPARATOR = ".";
    private static final I18nPath ROOT = new I18nPath(List.of());

    @NotNull
    static public I18nPath root() {
        return ROOT;
    }

    @NotNull
    static public I18nPath of(@NotNull String... path) {
        expectNonNull(path, "path");
        List<String> segments = Arrays.asList(path);
        validate(segments);
        return create(segments);
    }

    @NotNull
    static public I18nPath ofNullable(@NotNull String... path) {
        expectNonNull(path, "path");
        List<String> normalized = Arrays.stream(path)
                .filter(segment -> !segment.isBlank())
                .toList();
        validate(normalized);
        return create(normalized);
    }

    @NotNull
    static public I18nPath of(@NotNull List<String> path) {
        validate(path);
        return create(path);
    }

    @NotNull
    static public I18nPath of(@NotNull String path) {
        validate(path);
        return create(split(path));
    }

    static public void validate(@NotNull String path) {
        expectNonNull(path, "path");
        if (path.contains("..")) {
            throw new IllegalArgumentException("Path must not contain: \"..\"");
        }
        if (path.startsWith(".")) {
            throw new IllegalArgumentException("Path must not start with: \".\"");
        }
        if (path.endsWith(".")) {
            throw new IllegalArgumentException("Path must not end with: \".\"");
        }
        validate(split(path));
    }

    static private void validate(@NotNull List<String> segments) {
        expectNonNull(segments, "segments");
        segments.forEach(segment -> validateSegment(segment, segments));
    }

    static private void validateSegment(@NotNull String segment, List<String> segments) {
        expectNonNull(segment, "segment");
        Supplier<String> path = () -> String.join(SEPARATOR, segments);
        for (int c : segment.toCharArray()) {
            if (!WHITELIST_CHARS.contains(c)) {
                throw new IllegalArgumentException("Invalid character '" + (char) c + "' in path segment: " + segment
                        + " in path: " + path.get());
            }
        }
    }

    static private List<String> split(String path) {
        expectNonNull(path, "path");
        if (path.equals(SEPARATOR)) {
            return Arrays.asList("", "");
        }
        return Collections.list(new StringTokenizer(path, SEPARATOR)).stream()
                .map(token -> (String) token)
                .collect(toList());
    }

    static private I18nPath create(List<String> segments) {
        return segments.isEmpty()
                ? ROOT
                : new I18nPath(segments);
    }

    private final String path;
    private final List<String> segments;

    private I18nPath(List<String> segments) {
        expectNonNull(segments, "segments");
        this.path = String.join(SEPARATOR, segments);
        this.segments = List.copyOf(segments);
    }

    public boolean isRoot() {
        return path.isEmpty();
    }

    @NotNull
    public I18nPath child(@NotNull String subPath) {
        if (subPath.isBlank()) {
            return this;
        }
        return child(I18nPath.of(subPath));
    }

    @NotNull
    public List<String> getSegments() {
        return segments;
    }

    @NotNull
    public String getLastSegment() {
        if (segments.isEmpty()) {
            throw new IllegalStateException("Empty path");
        }
        return segments.get(segments.size() - 1);
    }

    @NotNull
    public I18nPath child(@NotNull I18nPath subPath) {
        expectNonNull(subPath, "subPath");
        if (subPath.isRoot()) {
            return this;
        }
        List<String> newPath = new ArrayList<>();
        newPath.addAll(segments);
        newPath.addAll(subPath.segments);
        return new I18nPath(newPath);
    }

    @NotNull
    public I18nPath parentOrRoot() {
        if (segments.isEmpty()) {
            return ROOT;
        }
        List<String> parentSegments = new ArrayList<>(segments);
        parentSegments.remove(parentSegments.size() - 1);
        return new I18nPath(parentSegments);
    }

    @NotNull
    public String getValue() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18nPath i18nPath = (I18nPath) o;
        return Objects.equals(path, i18nPath.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public String toShortString() {
        return path.isEmpty() ? "<ROOT>" : path;
    }

    @Override
    public String toString() {
        return toShortString();
    }
}
