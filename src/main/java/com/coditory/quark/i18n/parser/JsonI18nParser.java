package com.coditory.quark.i18n.parser;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.util.Locale;
import java.util.Map;

final class JsonI18nParser implements I18nParser {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    @NotNull
    public Map<I18nKey, String> parse(
            @NotNull String content,
            @Nullable I18nPath prefix,
            @Nullable Locale locale
    ) {
        Map<String, Object> entries = parseJson(content);
        return I18nParsers.parseEntries(entries, prefix, locale);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(@NotNull String content) {
        try {
            return gson.fromJson(content, Map.class);
        } catch (Throwable e) {
            throw new I18nParseException("Could not parse JSON", e);
        }
    }
}