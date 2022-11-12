package com.coditory.quark.i18n.api;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.coditory.quark.i18n.api.I18nPathGenerator.relaxedPathGenerator;
import static com.coditory.quark.i18n.api.I18nPathGenerator.strictPathGenerator;
import static com.coditory.quark.i18n.api.I18NLocaleGeneratorI18n.relaxedLocaleGenerator;
import static com.coditory.quark.i18n.api.I18NLocaleGeneratorI18n.strictLocalGenerator;

public interface I18nKeyGenerator {
    static I18nKeyGenerator relaxedI18nKeyGenerator() {
        return combine(
                relaxedLocaleGenerator(),
                relaxedPathGenerator()
        );
    }

    static I18nKeyGenerator relaxedI18nKeyGenerator(Locale defaultLocale) {
        return combine(
                relaxedLocaleGenerator(defaultLocale),
                relaxedPathGenerator()
        );
    }

    static I18nKeyGenerator strictI18nKeyGenerator() {
        return combine(
                strictLocalGenerator(),
                strictPathGenerator()
        );
    }

    static I18nKeyGenerator combine(I18nKeyGenerator... generators) {
        return new CombiningI18nKeyGenerator(Arrays.asList(generators));
    }

    List<I18nKey> keys(List<I18nPath> prefixes, I18nKey key);
}

class CombiningI18nKeyGenerator implements I18nKeyGenerator {
    private final List<I18nKeyGenerator> generators;

    public CombiningI18nKeyGenerator(List<I18nKeyGenerator> generators) {
        this.generators = List.copyOf(generators);
    }

    @Override
    public List<I18nKey> keys(List<I18nPath> prefixes, I18nKey key) {
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
