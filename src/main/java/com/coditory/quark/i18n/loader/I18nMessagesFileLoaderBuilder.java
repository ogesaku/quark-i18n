package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

public class I18nMessagesFileLoaderBuilder {
    private static final I18nKeyParser DEFAULT_KEY_PARSER = new DefaultI18nKeyParser();
    private static final Map<String, I18nParser> DEFAULT_FILE_PARSERS = Map.of(
            "yaml", YamlI18nParser.instance(),
            "yml", YamlI18nParser.instance(),
            "properties", PropertiesI18nParser.instance()
    );
    private final List<I18nPathPattern> locations = new ArrayList<>();
    private final Map<String, I18nParser> fileParsersByExtension = new HashMap<>(DEFAULT_FILE_PARSERS);
    private final Map<String, I18nKeyParser> keyParsersByExtension = new HashMap<>();
    private ClassLoader classLoader;
    private I18nParser fileParser;
    private I18nKeyParser keyParser = DEFAULT_KEY_PARSER;
    private I18nPath staticPrefix;

    public I18nMessagesFileLoaderBuilder scanClassPath() {
        return scanClassPath(Thread.currentThread().getContextClassLoader());
    }

    public I18nMessagesFileLoaderBuilder scanClassPath(ClassLoader classLoader) {
        this.classLoader = requireNonNull(classLoader);
        return this;
    }

    public I18nMessagesFileLoaderBuilder scanPathPattern(String filePattern) {
        requireNonNull(filePattern);
        I18nPathPattern template = I18nPathPattern.of(filePattern);
        this.locations.add(template);
        return this;
    }

    public I18nMessagesFileLoaderBuilder fileParser(String extension, I18nParser fileParser) {
        requireNonNull(extension);
        requireNonNull(fileParser);
        this.fileParsersByExtension.put(extension, fileParser);
        return this;
    }

    public I18nMessagesFileLoaderBuilder fileParser(I18nParser fileParser) {
        requireNonNull(fileParser);
        this.fileParser = fileParser;
        return this;
    }

    public I18nMessagesFileLoaderBuilder keyParser(String extension, I18nKeyParser keyParser) {
        requireNonNull(extension);
        requireNonNull(keyParser);
        this.keyParsersByExtension.put(extension, keyParser);
        return this;
    }

    public I18nMessagesFileLoaderBuilder keyParser(I18nKeyParser keyParser) {
        requireNonNull(keyParser);
        this.keyParser = keyParser;
        return this;
    }

    public I18nMessagesFileLoaderBuilder staticKeyPrefix(String prefix) {
        requireNonNull(keyParser);
        I18nPath path = I18nPath.of(prefix);
        return staticKeyPrefix(path);
    }

    public I18nMessagesFileLoaderBuilder staticKeyPrefix(I18nPath prefix) {
        requireNonNull(prefix);
        this.staticPrefix = prefix;
        return this;
    }

    public I18nMessagesFileLoader build() {
        return new I18nMessagesFileLoader(
                locations, classLoader, fileParser, fileParsersByExtension,
                staticPrefix, keyParser, keyParsersByExtension
        );
    }

    private static class YamlI18nParser implements I18nParser {
        private static final YamlI18nParser INSTANCE = new YamlI18nParser();

        static YamlI18nParser instance() {
            return INSTANCE;
        }

        @Override
        @NotNull
        public Map<String, Object> parse(@NotNull String content) {
            Yaml yaml = new Yaml();
            return yaml.load(content);
        }
    }

    private static class PropertiesI18nParser implements I18nParser {
        private static final PropertiesI18nParser INSTANCE = new PropertiesI18nParser();

        static PropertiesI18nParser instance() {
            return INSTANCE;
        }

        @Override
        @NotNull
        public Map<String, Object> parse(@NotNull String content) {
            Properties properties = new Properties();
            InputStream input = new ByteArrayInputStream(content.getBytes());
            try {
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (properties.isEmpty()) {
                return Map.of();
            }
            return properties.entrySet().stream()
                    .map(entry -> Map.entry(Objects.toString(entry.getKey()), entry.getValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
