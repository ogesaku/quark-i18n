package com.coditory.quark.i18n.api;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface I18NLocaleGeneratorI18n extends I18nKeyGenerator {
    static I18NLocaleGeneratorI18n strictLocalGenerator() {
        I18NLocaleGeneratorI18n generator = new StrictI18NLocaleGeneratorI18n();
        return new CachedI18NLocaleGeneratorI18n(generator);
    }

    static I18NLocaleGeneratorI18n relaxedLocaleGenerator() {
        I18NLocaleGeneratorI18n generator = new RelaxedI18NLocaleGeneratorI18n();
        return new CachedI18NLocaleGeneratorI18n(generator);
    }

    static I18NLocaleGeneratorI18n relaxedLocaleGenerator(Locale defaultLocale) {
        I18NLocaleGeneratorI18n generator = new RelaxedI18NLocaleGeneratorI18n(defaultLocale);
        return new CachedI18NLocaleGeneratorI18n(generator);
    }

    List<Locale> locales(Locale locale);

    default List<I18nKey> keys(List<I18nPath> prefixes, I18nKey key) {
        Locale locale = key.locale();
        return this.locales(key.locale()).stream()
                .map(l -> key.withLocale(locale))
                .collect(toList());
    }
}

class CachedI18NLocaleGeneratorI18n implements I18NLocaleGeneratorI18n {
    private final Map<Locale, List<Locale>> results = new ConcurrentHashMap<>();
    private final I18NLocaleGeneratorI18n generator;

    public CachedI18NLocaleGeneratorI18n(I18NLocaleGeneratorI18n generator) {
        this.generator = requireNonNull(generator);
    }

    @Override
    public List<Locale> locales(Locale locale) {
        return results.computeIfAbsent(locale, (__) -> generator.locales(locale));
    }
}

class StrictI18NLocaleGeneratorI18n implements I18NLocaleGeneratorI18n {
    @Override
    public List<Locale> locales(Locale locale) {
        return List.of(locale);
    }
}

class RelaxedI18NLocaleGeneratorI18n implements I18NLocaleGeneratorI18n {
    private final Locale defaultLocale;
    private final Locale defaultLocaleWithLangOnly;

    public RelaxedI18NLocaleGeneratorI18n() {
        this(null);
    }

    public RelaxedI18NLocaleGeneratorI18n(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
        this.defaultLocaleWithLangOnly =
                defaultLocale != null && defaultLocale.hasExtensions()
                        ? new Locale(defaultLocale.getLanguage())
                        : null;
    }

    @Override
    public List<Locale> locales(Locale locale) {
        Locale languageOnly = new Locale(locale.getLanguage());
        return defaultLocale != null
                ? uniqueNonNull(locale, languageOnly, defaultLocale, defaultLocaleWithLangOnly)
                : uniqueNonNull(locale, languageOnly);
    }

    private List<Locale> uniqueNonNull(Locale... locales) {
        return Arrays.stream(locales)
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());
    }
}
