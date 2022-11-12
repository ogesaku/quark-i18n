package com.coditory.quark.i18n.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


public class I18nPath {
    private static final String SEPARATOR = ".";
    private static final I18nPath ROOT = new I18nPath(List.of());

    static public I18nPath root() {
        return ROOT;
    }

    static public I18nPath of(List<String> chunks) {
        return create(chunks);
    }

    static public I18nPath of(String path) {
        return create(split(path));
    }

    static public I18nPath of(String... path) {
        return create(asList(path));
    }

    static public List<String> split(String path) {
        return Collections.list(new StringTokenizer(path, SEPARATOR)).stream()
                .map(token -> (String) token)
                .collect(toList());
    }

    static public String join(List<String> chunks) {
        return chunks.stream()
                .filter(c -> c != null && !c.isBlank())
                .collect(joining(SEPARATOR));
    }

    static private I18nPath create(List<String> chunks) {
        List<String> normalized = chunks.stream()
                .filter(c -> c != null && !c.isBlank())
                .collect(toList());
        return normalized.isEmpty()
                ? ROOT
                : new I18nPath(normalized);
    }

    private final String path;
    private final List<String> chunks;

    private I18nPath(List<String> chunks) {
        this.path = join(chunks);
        this.chunks = List.copyOf(chunks);
    }

    boolean isRoot() {
        return path.isEmpty();
    }

    public I18nPath child(String subPath) {
        List<String> chunks = new ArrayList<>(this.chunks);
        chunks.add(subPath);
        return new I18nPath(chunks);
    }

    public I18nPath child(I18nPath subPath) {
        return child(subPath.path);
    }

    public I18nPath parentOrRoot() {
        if (chunks.isEmpty()) {
            return ROOT;
        }
        List<String> parentChunks = new ArrayList<>(chunks);
        parentChunks.remove(parentChunks.size() - 1);
        return new I18nPath(parentChunks);
    }

    public List<I18nPath> parents() {
        List<I18nPath> result = new ArrayList<>(chunks.size());
        I18nPath parent = parentOrRoot();
        while (!parent.isRoot()) {
            result.add(parent);
            parent = parent.parentOrRoot();
        }
        result.add(parent);
        return Collections.unmodifiableList(result);
    }

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
