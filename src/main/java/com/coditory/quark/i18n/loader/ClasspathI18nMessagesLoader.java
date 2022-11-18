package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nPath;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;

public class ClasspathI18nMessagesLoader implements I18nMessagesLoader {
    private final List<String> pathTemplates = new ArrayList<>();
    private final I18nMessagesParser parser;

    public ClasspathI18nMessagesLoader(I18nMessagesParser parser) {
        this.parser = parser;
    }

    @Override
    public Map<I18nPath, String> load() {
        Map<I18nPath, String> result = new HashMap<>();
        for (String path : pathTemplates) {
            result.putAll(load(path));
        }
        return result;
    }

    private Map<I18nPath, String> load(String pathTemplate) {
        pathTemplate = File.separator.equals("\\") ? pathTemplate.replace("\\", "/") : pathTemplate;
        return null;
    }

    private Map<I18nPath, String> toRegex(String pathTemplate) {
        return null;
    }

    private static class PathTemplate {
        private static final Set<Integer> WHITELIST_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789/-_.*"
                .chars()
                .boxed()
                .collect(toSet());
        private static final String PREFIX_GROUP_MARKER = "{prefix}";
        private static final String LOCALE_GROUP_MARKER = "{locale}";
        private final String path;
        private final Pattern pattern;
        private final Pattern dirPattern;

        public static PathTemplate of(String path) {
            validate(path);
            String pathEscaped = path
                    .replace(".", "\\.")
                    .replace("*", "[a-zA-Z0-9_-.]+")
                    .replace("**/", "([a-zA-Z0-9_-./]+/)?")
                    .replace(PREFIX_GROUP_MARKER, "(?<prefix>[a-zA-Z0-9_-.]+)")
                    .replace(LOCALE_GROUP_MARKER, "(?<locale>(?<lang>[a-z]+)(_(?<country>[A-Z]+))?)");
            Pattern pattern = Pattern.compile(pathEscaped);
            // Pattern dirPattern = Pattern.compile(pathEscaped.substring(0, ));
            return null;
        }

        private static void validate(String path) {
            if (path == null || path.isBlank()) {
                throw new IllegalArgumentException("Expected non-blank path");
            }
            if (path.endsWith("/")) {
                throw new IllegalArgumentException("Expected path to not end with /. Path: " + path);
            }
            if (path.indexOf(PREFIX_GROUP_MARKER) != path.lastIndexOf(PREFIX_GROUP_MARKER)) {
                throw new IllegalArgumentException("Expected at most one " + PREFIX_GROUP_MARKER + " in path: " + path);
            }
            if (path.indexOf(LOCALE_GROUP_MARKER) != path.lastIndexOf(LOCALE_GROUP_MARKER)) {
                throw new IllegalArgumentException("Expected at most one " + LOCALE_GROUP_MARKER + " in path: " + path);
            }
            if (path.contains("\\")) {
                throw new IllegalArgumentException("Expected unix file separators. Path: " + path);
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
                    throw new IllegalArgumentException("Invalid character in path template: '" + c + "'. Path: " + path);
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

        private PathTemplate(String path, Pattern pattern, Pattern dirPattern) {
            this.path = path;
            this.pattern = pattern;
            this.dirPattern = dirPattern;
        }
    }
}
