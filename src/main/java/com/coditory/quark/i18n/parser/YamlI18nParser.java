package com.coditory.quark.i18n.parser;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.util.Locale;
import java.util.Map;

final class YamlI18nParser implements I18nParser {
    @Override
    @NotNull
    public Map<I18nKey, String> parse(
            @NotNull String content,
            @Nullable I18nPath prefix,
            @Nullable Locale locale
    ) {
        Map<String, Object> entries = parseYaml(content);
        return I18nParsers.parseEntries(entries, prefix, locale);
    }

    private Map<String, Object> parseYaml(@NotNull String content) {
        try {
            Yaml yaml = new Yaml();
            return yaml.load(content);
        } catch (Throwable e) {
            throw new I18nParseException("Could not parse YAML", e);
        }
    }
}