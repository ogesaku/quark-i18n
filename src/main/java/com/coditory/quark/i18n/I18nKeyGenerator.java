package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.coditory.quark.i18n.I18NLocaleGeneratorI18n.relaxedLocaleGenerator;
import static com.coditory.quark.i18n.I18NLocaleGeneratorI18n.strictLocalGenerator;
import static com.coditory.quark.i18n.I18nPathGenerator.relaxedPathGenerator;
import static com.coditory.quark.i18n.I18nPathGenerator.strictPathGenerator;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public interface  I18nKeyGenerator {
    @NotNull
    static I18nKeyGenerator relaxedI18nKeyGenerator() {
        return combine(
                relaxedLocaleGenerator(),
                relaxedPathGenerator()
        );
    }

    @NotNull
    static I18nKeyGenerator relaxedI18nKeyGenerator(@Nullable Locale defaultLocale) {
        expectNonNull(defaultLocale, "defaultLocale");
        return combine(
                relaxedLocaleGenerator(defaultLocale),
                relaxedPathGenerator()
        );
    }

    @NotNull
    static I18nKeyGenerator strictI18nKeyGenerator() {
        return combine(
                strictLocalGenerator(),
                strictPathGenerator()
        );
    }

    @NotNull
    static I18nKeyGenerator combine(I18nKeyGenerator... generators) {
        expectNonNull(generators, "generators");
        return new CombiningI18nKeyGenerator(Arrays.asList(generators));
    }

    @NotNull
    List<I18nKey> keys(@NotNull List<I18nPath> prefixes, @NotNull I18nKey key);
}

class CombiningI18nKeyGenerator implements I18nKeyGenerator {
    private final List<I18nKeyGenerator> generators;

    public CombiningI18nKeyGenerator(@NotNull List<I18nKeyGenerator> generators) {
        this.generators = List.copyOf(generators);
    }

    @NotNull
    @Override
    public List<I18nKey> keys(@NotNull List<I18nPath> prefixes, @NotNull I18nKey key) {
        expectNonNull(prefixes, "prefixes");
        expectNonNull(key, "key");
        Set<I18nKey> result = new LinkedHashSet<>();
        result.add(key);
        for (I18nKeyGenerator generator : generators) {
            for (I18nKey k : result) {
                result.addAll(generator.keys(prefixes, k));
            }
        }
        return List.copyOf(result);
    }
}
