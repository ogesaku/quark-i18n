package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.toList;

public interface I18NLocaleGeneratorI18n extends I18nKeyGenerator {
    @NotNull
    static I18NLocaleGeneratorI18n strictLocalGenerator() {
        I18NLocaleGeneratorI18n generator = new StrictI18NLocaleGeneratorI18n();
        return new CachedI18NLocaleGeneratorI18n(generator);
    }

    @NotNull
    static I18NLocaleGeneratorI18n relaxedLocaleGenerator() {
        I18NLocaleGeneratorI18n generator = new RelaxedI18NLocaleGeneratorI18n();
        return new CachedI18NLocaleGeneratorI18n(generator);
    }

    @NotNull
    static I18NLocaleGeneratorI18n relaxedLocaleGenerator(@NotNull Locale defaultLocale) {
        I18NLocaleGeneratorI18n generator = new RelaxedI18NLocaleGeneratorI18n(defaultLocale);
        return new CachedI18NLocaleGeneratorI18n(generator);
    }

    @NotNull
    List<Locale> locales(@NotNull Locale locale);

    @NotNull
    default List<I18nKey> keys(@NotNull List<I18nPath> prefixes, @NotNull I18nKey key) {
        expectNonNull(prefixes, "prefixes");
        expectNonNull(key, "key");
        return this.locales(key.locale())
                .stream()
                .map(key::withLocale)
                .collect(toList());
    }
}

class CachedI18NLocaleGeneratorI18n implements I18NLocaleGeneratorI18n {
    private final Map<Locale, List<Locale>> results = new ConcurrentHashMap<>();
    private final I18NLocaleGeneratorI18n generator;

    public CachedI18NLocaleGeneratorI18n(@NotNull I18NLocaleGeneratorI18n generator) {
        this.generator = expectNonNull(generator, "generator");
    }

    @Override
    @NotNull
    public List<Locale> locales(@NotNull Locale locale) {
        expectNonNull(locale, "locale");
        return results.computeIfAbsent(locale, (__) -> generator.locales(locale));
    }
}

class StrictI18NLocaleGeneratorI18n implements I18NLocaleGeneratorI18n {
    @Override
    @NotNull
    public List<Locale> locales(@NotNull Locale locale) {
        expectNonNull(locale, "locale");
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
    @NotNull
    public List<Locale> locales(@NotNull Locale locale) {
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
