package com.coditory.quark.i18n.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toCollection;

final class FileScanner implements Iterator<File> {
    public static FileScanner scanFiles(String firstPattern, String... otherPatterns) {
        FileScannerBuilder builder = builder();
        builder.scanLocation(firstPattern);
        Arrays.stream(otherPatterns)
                .forEach(builder::scanLocation);
        return builder.build();
    }

    public static FileScanner scanClassPath(String firstPattern, String... otherPatterns) {
        return scanClassPath(Thread.currentThread().getContextClassLoader(), firstPattern, otherPatterns);
    }

    public static FileScanner scanClassPath(ClassLoader classLoader, String firstPattern, String... otherPatterns) {
        FileScannerBuilder builder = builder();
        builder
                .scanClassPath(classLoader)
                .scanLocation(firstPattern);
        Arrays.stream(otherPatterns)
                .forEach(builder::scanLocation);
        return builder.build();
    }

    private final Queue<File> files;
    private final Predicate<File> filter;
    private File next;

    private FileScanner(List<File> files, Predicate<File> filter) {
        this.files = files.stream()
                .distinct()
                .filter(filter)
                .collect(toCollection(LinkedList::new));
        this.filter = filter;
        this.next = findNext();
    }

    public Stream<File> stream() {
        FileScanner copy = new FileScanner(List.copyOf(files), filter);
        return StreamSupport.stream(spliteratorUnknownSize(copy, Spliterator.ORDERED), false);
    }

    public List<File> toList() {
        return stream().toList();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public File next() {
        if (next == null) {
            throw new NoSuchElementException("No more files to scan");
        }
        File result = next;
        next = findNext();
        return result;
    }

    private File findNext() {
        File result = null;
        while (result == null && !files.isEmpty()) {
            File file = files.poll();
            if (!file.exists()) {
                break;
            }
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children == null) {
                    break;
                }
                for (File child : children) {
                    if (filter.test(child)) {
                        files.add(child);
                    }
                }
            } else {
                result = file;
            }
        }
        return result;
    }

    public static FileScannerBuilder builder() {
        return new FileScannerBuilder();
    }

    public static class FileScannerBuilder {
        private final List<String> locations = new ArrayList<>();
        private final List<Predicate<String>> filters = new ArrayList<>();
        private boolean combineFiltersWithOr = true;
        private ClassLoader classLoader;

        public FileScannerBuilder scanClassPath() {
            return scanClassPath(Thread.currentThread().getContextClassLoader());
        }

        public FileScannerBuilder scanClassPath(ClassLoader classLoader) {
            this.classLoader = requireNonNull(classLoader);
            return this;
        }

        public FileScannerBuilder scanLocations(List<String> patterns) {
            requireNonNull(patterns);
            patterns.forEach(this::scanLocation);
            return this;
        }

        public FileScannerBuilder scanLocation(String pattern) {
            requireNonNull(pattern);
            if (pattern.contains("\\")) {
                throw new IllegalArgumentException("Expected only forward slashes");
            }
            String dir = SimpleFilePattern.extractBaseDir(pattern);
            if (!dir.isBlank()) {
                locations.add(dir);
            }
            if (!Objects.equals(dir, pattern)) {
                filter(pattern);
            }
            return this;
        }

        public FileScannerBuilder filter(Predicate<String> filter) {
            requireNonNull(filter);
            filters.add(path -> {
                String normalizedPath = normalizePath(path);
                return filter.test(normalizedPath);
            });
            return this;
        }

        private String normalizePath(String path) {
            return File.separatorChar == '\\'
                    ? path.replace("\\", "/")
                    : path;
        }

        public FileScannerBuilder filter(List<String> patterns) {
            requireNonNull(patterns);
            patterns.forEach(this::filter);
            return this;
        }

        public FileScannerBuilder filter(String pattern) {
            requireNonNull(pattern);
            if (pattern.contains("\\")) {
                throw new IllegalArgumentException("Expected unix file separators");
            }
            Predicate<String> predicate = SimpleFilePattern.compile(pattern, '/')
                    .asMatchPredicate();
            return filter(predicate);
        }

        public FileScannerBuilder filterWithRegex(String regex) {
            requireNonNull(regex);
            Pattern pattern = Pattern.compile(regex);
            return filterWithRegex(pattern);
        }

        public FileScannerBuilder filterWithRegex(Pattern pattern) {
            requireNonNull(pattern);
            Predicate<String> namePredicate = pattern.asMatchPredicate();
            return filter(namePredicate);
        }

        public FileScannerBuilder combineFiltersWithAnd() {
            combineFiltersWithOr = false;
            return this;
        }

        public FileScanner build() {
            return classLoader != null
                    ? buildForClassPath()
                    : buildForFiles();
        }

        private FileScanner buildForFiles() {
            List<File> files = locations.stream()
                    .map(File::new)
                    .distinct()
                    .toList();
            return new FileScanner(files, aggregateFilters());
        }

        private FileScanner buildForClassPath() {
            List<File> files = locations.isEmpty()
                    ? listFilesFromClassPath(classLoader, ".")
                    : locations.stream()
                    .flatMap(location -> listFilesFromClassPath(classLoader, location).stream())
                    .distinct()
                    .toList();
            return new FileScanner(files, aggregateFilters());
        }

        private Predicate<File> aggregateFilters() {
            Predicate<String> aggregated = filters.stream()
                    .reduce(f -> true, (a, c) -> combineFiltersWithOr ? c.or(a) : c.and(a));
            return (file) -> aggregated.test(normalizePath(file.getAbsolutePath()));
        }

        private List<File> listFilesFromClassPath(ClassLoader classLoader, String path) {
            List<File> files = new ArrayList<>();
            try {
                Enumeration<URL> resources = classLoader.getResources(path);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    files.add(new File(resource.getFile()));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not get resource from classpath", e);
            }
            return files;
        }
    }
}
