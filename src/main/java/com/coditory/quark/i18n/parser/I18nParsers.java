package com.coditory.quark.i18n.parser;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public final class I18nParsers {
    private static final EntriesI18nParser I18N_KEY_PARSER = new EntriesI18nParser();
    public static final I18nParser PROPERTIES_I18N_PARSER = new PropertiesI18nParser();
    public static final I18nParser YAML_I18N_PARSER = new YamlI18nParser();
    public static final I18nParser JSON_I18N_PARSER = new JsonI18nParser();

    public static final Map<String, I18nParser> I18N_PARSERS_BY_EXT = Map.of(
            "yaml", YAML_I18N_PARSER,
            "yml", YAML_I18N_PARSER,
            "json", JSON_I18N_PARSER,
            "properties", PROPERTIES_I18N_PARSER
    );

    @NotNull
    public static Map<I18nKey, String> parseYaml(@NotNull String content, @Nullable I18nPath prefix, @Nullable Locale locale) {
        return YAML_I18N_PARSER.parse(content, prefix, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseYaml(@NotNull String content, @Nullable I18nPath prefix) {
        return parseYaml(content, prefix, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseYaml(@NotNull String content, @Nullable Locale locale) {
        return parseYaml(content, null, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseYaml(@NotNull String content) {
        return parseYaml(content, null, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseProperties(@NotNull String content, @Nullable I18nPath prefix, @Nullable Locale locale) {
        return PROPERTIES_I18N_PARSER.parse(content, prefix, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseProperties(@NotNull String content, @Nullable I18nPath prefix) {
        return parseProperties(content, prefix, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseProperties(@NotNull String content, @Nullable Locale locale) {
        return parseProperties(content, null, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseProperties(@NotNull String content) {
        return parseProperties(content, null, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseJson(@NotNull String content, @Nullable I18nPath prefix, @Nullable Locale locale) {
        return JSON_I18N_PARSER.parse(content, prefix, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseJson(@NotNull String content, @Nullable I18nPath prefix) {
        return parseJson(content, prefix, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseJson(@NotNull String content, @Nullable Locale locale) {
        return parseJson(content, null, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseJson(@NotNull String content) {
        return parseJson(content, null, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseEntries(@NotNull Map<String, Object> entries, @Nullable I18nPath prefix, @Nullable Locale locale) {
        return I18N_KEY_PARSER.parseEntries(entries, prefix, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseEntries(@NotNull Map<String, Object> entries, @Nullable I18nPath prefix) {
        return parseEntries(entries, prefix, null);
    }

    @NotNull
    public static Map<I18nKey, String> parseEntries(@NotNull Map<String, Object> entries, @Nullable Locale locale) {
        return parseEntries(entries, null, locale);
    }

    @NotNull
    public static Map<I18nKey, String> parseEntries(@NotNull Map<String, Object> entries) {
        return parseEntries(entries, null, null);
    }
}
