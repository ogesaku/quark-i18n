package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import static com.coditory.quark.i18n.Preconditions.expect;
import static com.coditory.quark.i18n.Preconditions.expectNonBlank;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public final class I18nPath {
    private static final String SEPARATOR = ".";
    private static final I18nPath ROOT = new I18nPath(List.of());

    @NotNull
    static public I18nPath root() {
        return ROOT;
    }

    @NotNull
    static public I18nPath of(@NotNull List<String> segments) {
        expectNonNull(segments, "segments");
        return create(segments);
    }

    @NotNull
    static public I18nPath of(@NotNull String path) {
        expectNonNull(path, "path");
        return create(split(path));
    }

    static public void validate(@NotNull String path) {
        validate(split(path));
    }

    static public void validate(@NotNull List<String> segments) {
        expectNonNull(segments, "segments");
        String joined = String.join(SEPARATOR, segments);
        segments.forEach(segment -> expect(segment != null && !segment.isBlank(), "Expected non-blank path segments in: " + joined));
    }

    @NotNull
    static public I18nPath of(@NotNull String... path) {
        expectNonNull(path, "path");
        return create(asList(path));
    }

    @NotNull
    static public I18nPath ofNullable(@NotNull String... path) {
        expectNonNull(path, "path");
        List<String> normalized = Arrays.stream(path)
                .filter(segment -> segment != null && !segment.isBlank())
                .toList();
        return create(normalized);
    }

    @NotNull
    static public List<String> split(@NotNull String path) {
        expectNonNull(path, "path");
        return Collections.list(new StringTokenizer(path, SEPARATOR)).stream()
                .map(token -> (String) token)
                .collect(toList());
    }

    static private I18nPath create(List<String> segments) {
        validate(segments);
        return segments.isEmpty()
                ? ROOT
                : new I18nPath(segments);
    }

    private final String path;
    private final List<String> segments;

    private I18nPath(List<String> segments) {
        expectNonNull(segments, "segments");
        String joined = String.join(SEPARATOR, segments);
        segments.forEach(segment -> expect(segment != null && !segment.isBlank(), "Expected non-blank path segments in: " + joined));
        this.path = joined;
        this.segments = List.copyOf(segments);
    }

    public boolean isRoot() {
        return path.isEmpty();
    }

    @NotNull
    public I18nPath child(@NotNull String subPath) {
        expectNonBlank(subPath, "subPath");
        List<String> segments = new ArrayList<>(this.segments);
        segments.add(subPath);
        return new I18nPath(segments);
    }

    @NotNull
    public I18nPath child(@NotNull I18nPath subPath) {
        expectNonNull(subPath, "subPath");
        return child(subPath.path);
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
    public List<I18nPath> parents() {
        List<I18nPath> result = new ArrayList<>(segments.size());
        I18nPath parent = parentOrRoot();
        while (!parent.isRoot()) {
            result.add(parent);
            parent = parent.parentOrRoot();
        }
        result.add(parent);
        return Collections.unmodifiableList(result);
    }

    @NotNull
    public String getPathValue() {
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

    @Override
    public String toString() {
        return path.isEmpty()
                ? "ROOT"
                : path;
    }
}
