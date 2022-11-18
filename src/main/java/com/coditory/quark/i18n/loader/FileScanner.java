package com.coditory.quark.i18n.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

final class FileScanner implements Iterator<File> {
    public static void main(String[] args) {
        matches("/abc/x123.json", "/abc/**/y*", '/');
    }

    public static void matches(String input, String pattern, char separator) {
        String escapedSeparator = separator == '/' ? "/" : "\\\\";
        String regex = split(pattern, "\\*\\*+" + escapedSeparator)
                .map(chunk -> split(chunk, "\\*")
                        .map(subchunk -> subchunk.isEmpty() ? subchunk : Pattern.quote(subchunk))
                        .collect(joining("[^" + escapedSeparator + "]*"))
                ).collect(joining("(.*/)?"));
        System.out.println("Input: " + input + " Pattern: " + pattern + " Regex: " + regex + " Matches: " + input.matches(regex));
    }

    private static Stream<String> split(String input, String separator) {
        Stream<String> chunks = Arrays.stream(input.split(separator));
        return input.matches(".*" + separator + "$")
                ? Stream.concat(chunks, Stream.of(""))
                : chunks;
    }

    public static void main2(String[] args) throws IOException {
        List<File> result = listFilesOnClassPath(FileScanner.class.getClassLoader(), ".", (file) -> {
            System.out.println("Predicate for: " + file.getAbsolutePath());
            return true;
        });
        System.out.println("RESULT:");
        for (File file : result) {
            System.out.println(file.getName());
        }
    }

    private static List<File> listFilesOnClassPath(ClassLoader classLoader, String path, Predicate<File> filter) throws IOException {
        Iterator<File> iterator = findFilesOnClassPath(classLoader, path, filter);
        List<File> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    private static Iterator<File> findFilesOnClassPath(ClassLoader classLoader, String path, Predicate<File> filter) throws IOException {
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> locations = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            locations.add(new File(resource.getFile()));
        }
        return new FileScanner(locations, filter);
    }

    private final Queue<File> files;
    private final Predicate<File> filter;
    private File next;

    private FileScanner(List<File> filesToScan, Predicate<File> filter) {
        this.files = new LinkedList<>();
        this.filter = filter;
        for (File file : filesToScan) {
            if (filter.test(file)) {
                files.add(file);
            }
        }
        next = findNext();
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

    public static class FileScannerBuilder {
        private final List<File> locationsToScan = new ArrayList<>();
        private final List<Predicate<File>> filters = new ArrayList<>();
        private boolean combineFiltersWithOr = true;

        public FileScannerBuilder addLocationToScan(String location) {
            requireNonNull(location);
            return addLocationToScan(new File(location));
        }

        public FileScannerBuilder addLocationToScan(Path path) {
            requireNonNull(path);
            return addLocationToScan(path.toFile());
        }

        public FileScannerBuilder addLocationToScan(File location) {
            requireNonNull(location);
            locationsToScan.add(location);
            return this;
        }

        public FileScannerBuilder addClassPathToScan(ClassLoader classLoader) {
            requireNonNull(classLoader);
            return addClassPathToScan(classLoader, ".");
        }

        public FileScannerBuilder addClassPathToScan(ClassLoader classLoader, String path) {
            requireNonNull(classLoader);
            requireNonNull(path);
            try {
                Enumeration<URL> resources = classLoader.getResources(path);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    locationsToScan.add(new File(resource.getFile()));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not get resource from classpath", e);
            }
            return this;
        }

        public FileScannerBuilder addFilter(Predicate<File> filter) {
            requireNonNull(filter);
            filters.add(filter);
            return this;
        }

        public FileScannerBuilder addRegexFilter(String regex) {
            requireNonNull(regex);
            Pattern pattern = Pattern.compile(regex);
            return addRegexFilter(pattern);
        }

        public FileScannerBuilder addRegexFilter(Pattern pattern) {
            requireNonNull(pattern);
            Predicate<String> namePredicate = pattern.asMatchPredicate();
            return addFilter(file -> namePredicate.test(file.getAbsolutePath()));
        }

        public FileScannerBuilder addFilePatternFilter(String pattern) {
            // TODO: Finish
            // filters.add(filter);
            return this;
        }

        public FileScannerBuilder combineFiltersWithOr() {
            combineFiltersWithOr = true;
            return this;
        }

        public FileScannerBuilder combineFiltersWithAnd() {
            combineFiltersWithOr = false;
            return this;
        }

        public FileScanner build() {
            Predicate<File> aggregatedFilter = filters.stream()
                    .reduce(f -> true, (a, c) -> combineFiltersWithOr ? c.or(a) : c.and(a));
            return new FileScanner(locationsToScan, aggregatedFilter);
        }
    }
}
