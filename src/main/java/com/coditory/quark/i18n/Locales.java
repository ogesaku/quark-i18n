package com.coditory.quark.i18n;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class Locales {
    private static final Set<Locale> AVAILABLE_LOCALES = Set.of(Locale.getAvailableLocales());
    /**
     * English
     */
    public static Locale EN = Locale.forLanguageTag("en");

    /**
     * English (United Kingdom)
     */
    public static Locale EN_GB = Locale.forLanguageTag("en-GB");

    /**
     * English (United States)
     */
    public static Locale EN_US = Locale.forLanguageTag("en-US");

    /**
     * French
     */
    public static Locale FR = Locale.forLanguageTag("fr");

    /**
     * French (France)
     */
    public static Locale FR_FR = Locale.forLanguageTag("fr-FR");

    /**
     * Japanese
     */
    public static Locale JA = Locale.forLanguageTag("ja");

    /**
     * Japanese (Japan)
     */
    public static Locale JA_JP = Locale.forLanguageTag("ja-JP");

    /**
     * German
     */
    public static Locale DE = Locale.forLanguageTag("de");

    /**
     * German (Germany)
     */
    public static Locale DE_DE = Locale.forLanguageTag("de-DE");

    /**
     * Chinese
     */
    public static Locale ZH = Locale.forLanguageTag("zh");

    /**
     * Chinese (China)
     */
    public static Locale ZH_CN = Locale.forLanguageTag("zh-CN");

    /**
     * Polish
     */
    public static Locale PL = Locale.forLanguageTag("pl");

    /**
     * Polish (Poland)
     */
    public static Locale PL_PL = Locale.forLanguageTag("pl-PL");

    private Locales() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    public static boolean isAvailable(Locale locale) {
        expectNonNull(locale, "locale");
        return AVAILABLE_LOCALES.contains(locale);
    }

    public static Locale parseLocale(String value) {
        expectNonNull(value, "value");
        Locale locale;
        try {
            locale = Locale.forLanguageTag(value.replace("_", "-").trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse Locale: '" + value + "'");
        }
        if (!isAvailable(locale)) {
            throw new IllegalArgumentException("Locale not available: '" + value + "'");
        }
        return locale;
    }

    public static Locale parseLocaleOrNull(String value) {
        try {
            return parseLocale(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Locale parseLocaleOrDefault(String value, Locale defaultLocale) {
        Locale result = parseLocaleOrNull(value);
        return result == null ? defaultLocale : result;
    }

    public static Optional<Locale> parseLocaleOrEmpty(String value) {
        return Optional.ofNullable(parseLocaleOrNull(value));
    }
}
