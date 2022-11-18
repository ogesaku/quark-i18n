package com.coditory.quark.i18n.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

final class ClasspathScanner {
    static List<File> scanClassPath(Predicate<File> filter, ClassLoader classLoader) {
        try {
            return findFiles(filter, classLoader);
        } catch (IOException e) {
            throw new RuntimeException("Could not scan classpath", e);
        }
    }

    private static List<File> findFiles(Predicate<File> filter, ClassLoader classLoader)
            throws IOException {
        Enumeration<URL> resources = classLoader.getResources("/");
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<File> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findFiles(directory, filter));
        }
        return classes;
    }

    private static List<File> findFiles(File directory, Predicate<File> filter) {
        List<File> result = new ArrayList<>();
        if (!directory.exists()) {
            return result;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return result;
        }
        for (File file : files) {
            if (filter.test(file)) {
                if (file.isDirectory()) {
                    result.addAll(findFiles(file, filter));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
