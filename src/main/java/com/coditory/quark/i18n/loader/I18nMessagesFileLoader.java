package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import com.coditory.quark.i18n.loader.I18nPathPattern.I18nPathGroups;
import com.coditory.quark.i18n.parser.I18nParser;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class I18nMessagesFileLoader implements I18nMessagesLoader {
    @NotNull
    public static I18nMessagesFileLoader scanClassPath(@NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        return scanClassPath(Thread.currentThread().getContextClassLoader(), firstPattern, others);
    }

    @NotNull
    public static I18nMessagesFileLoader scanClassPath(@NotNull ClassLoader classLoader, @NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        requireNonNull(classLoader);
        I18nMessagesFileLoaderBuilder builder = I18nMessagesFileLoader.builder()
                .classLoader(classLoader)
                .scanPathPattern(firstPattern);
        for (String other : others) {
            builder.scanPathPattern(other);
        }
        return builder.build();
    }

    @NotNull
    public static I18nMessagesFileLoader scanFileSystem(@NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        return scanFileSystem(FileSystems.getDefault(), firstPattern, others);
    }

    @NotNull
    public static I18nMessagesFileLoader scanFileSystem(@NotNull FileSystem fileSystem, @NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        requireNonNull(fileSystem);
        I18nMessagesFileLoaderBuilder builder = I18nMessagesFileLoader.builder()
                .scanFileSystem(fileSystem)
                .scanPathPattern(firstPattern);
        for (String other : others) {
            builder.scanPathPattern(other);
        }
        return builder.build();
    }

    private final Set<I18nPathPattern> pathPatterns;
    private final ClassLoader classLoader;
    private final I18nParser parser;
    private final Map<String, I18nParser> parsersByExtension;
    private final I18nPath staticPrefix;
    private final FileSystem fileSystem;

    I18nMessagesFileLoader(
            Set<I18nPathPattern> pathPatterns,
            FileSystem fileSystem,
            ClassLoader classLoader,
            I18nParser fileParser,
            Map<String, I18nParser> parsersByExtension,
            I18nPath staticPrefix
    ) {
        this.classLoader = classLoader;
        this.staticPrefix = requireNonNull(staticPrefix);
        this.fileSystem = requireNonNull(fileSystem);
        this.pathPatterns = Set.copyOf(pathPatterns);
        this.parsersByExtension = Map.copyOf(parsersByExtension);
        this.parser = fileParser;
    }

    @Override
    public Map<I18nKey, String> load() {
        Map<I18nKey, String> result = new LinkedHashMap<>();
        for (I18nPathPattern pathPattern : pathPatterns) {
            List<Resource> resources = scanFiles(pathPattern);
            for (Resource resource : resources) {
                System.out.println("Resource: " + resource);
                Map<I18nKey, String> parsed = parseFile(resource, pathPattern);
                result.putAll(parsed);
            }
        }
        return result;
    }

    private List<Resource> scanFiles(I18nPathPattern pathPattern) {
        return classLoader != null
                ? ResourceScanner.scanClassPath(classLoader, pathPattern)
                : ResourceScanner.scanFiles(fileSystem, pathPattern);
    }

    private Map<I18nKey, String> parseFile(Resource resource, I18nPathPattern pathPattern) {
        String extension = getExtension(resource.name());
        I18nParser parser = parsersByExtension.getOrDefault(extension, this.parser);
        if (parser == null) {
            throw new I18nLoadException("No file parser defined for: " + resource.name());
        }
        String content = readFile(resource);
        I18nPathGroups matchedGroups = pathPattern.matchGroups(resource.name());
        Locale locale = matchedGroups.locale();
        I18nPath prefix = matchedGroups.path() == null
                ? staticPrefix
                : staticPrefix.child(matchedGroups.path());
        try {
            return parser.parse(content, prefix, locale);
        } catch (Throwable e) {
            throw new I18nLoadException("Could not parse file: " + resource.name(), e);
        }
    }

    private String readFile(Resource resource) {
        try {
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.url().openStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }
            }
            return resultStringBuilder.toString();
        } catch (Throwable e) {
            throw new RuntimeException("Could not read classpath resource: " + resource.name(), e);
        }
    }

    private String getExtension(String resourceName) {
        int idx = resourceName.lastIndexOf('.');
        return idx == 0 || idx == resourceName.length() - 1
                ? null
                : resourceName.substring(idx + 1);
    }

    public static I18nMessagesFileLoaderBuilder builder() {
        return new I18nMessagesFileLoaderBuilder();
    }
}
