package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class I18nMessagesFileLoader implements I18nMessagesLoader {
    private final List<PathTemplate> locations;
    private final ClassLoader classLoader;
    private final I18nParser fileParser;
    private final Map<String, I18nParser> fileParsersByExtension;
    private final I18nKeyParser keyParser;
    private final Map<String, I18nKeyParser> keyParsersByExtension;
    private final I18nPath staticPrefix;

    I18nMessagesFileLoader(
            List<PathTemplate> locations,
            ClassLoader classLoader,
            I18nParser fileParser,
            Map<String, I18nParser> fileParsersByExtension,
            I18nPath staticPrefix,
            I18nKeyParser keyParser,
            Map<String, I18nKeyParser> keyParsersByExtension
    ) {
        this.locations = List.copyOf(locations);
        this.classLoader = classLoader;
        this.fileParser = fileParser;
        this.fileParsersByExtension = Map.copyOf(fileParsersByExtension);
        this.staticPrefix = staticPrefix;
        this.keyParser = keyParser;
        this.keyParsersByExtension = Map.copyOf(keyParsersByExtension);
    }

    @Override
    public Map<I18nKey, String> load() {
        Map<I18nKey, String> result = new LinkedHashMap<>();
        for (PathTemplate location : locations) {
            List<File> files = scanFiles(location);
            for (File file : files) {
                Map<I18nKey, String> fileResult = load(location, file);
                result.putAll(fileResult);
            }
        }
        return result;
    }

    private List<File> scanFiles(PathTemplate location) {
        FileScanner fileScanner = classLoader != null
                ? FileScanner.scanClassPath(classLoader, location.original())
                : FileScanner.scanFiles(location.original());
        return fileScanner.toList();
    }

    private Map<I18nKey, String> load(PathTemplate pathTemplate, File file) {
        Map<String, Object> parsed = parseFile(file);
        return parseKeys(pathTemplate, file, parsed);
    }

    private Map<String, Object> parseFile(File file) {
        String extension = getExtension(file);
        I18nParser parser = fileParsersByExtension.getOrDefault(extension, fileParser);
        if (parser == null) {
            throw new I18nParseException("No file parser defined for: " + file.getAbsolutePath());
        }
        String content = readFile(file);
        return parser.parse(content);
    }

    private String getExtension(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('.');
        return idx == 0 || idx == name.length() - 1
                ? null
                : name.substring(idx + 1);
    }

    private Map<I18nKey, String> parseKeys(PathTemplate pathTemplate, File file, Map<String, Object> parsed) {
        String extension = getExtension(file);
        I18nKeyParser parser = keyParsersByExtension.getOrDefault(extension, keyParser);
        if (parser == null) {
            throw new I18nParseException("No key parser defined for: " + file.getAbsolutePath());
        }
        Map<I18nKey, String> result = parser.parseKeys(pathTemplate, file, parsed);
        if (staticPrefix != null) {
            Map<I18nKey, String> prefixed = new LinkedHashMap<>();
            for (Map.Entry<I18nKey, String> entry : result.entrySet()) {
                I18nKey path = entry.getKey().prefix(staticPrefix);
                prefixed.put(path, entry.getValue());
            }
            result = prefixed;
        }
        return result;
    }

    private String readFile(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }
            }
            return resultStringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static I18nMessagesFileLoaderBuilder builder() {
        return new I18nMessagesFileLoaderBuilder();
    }
}
