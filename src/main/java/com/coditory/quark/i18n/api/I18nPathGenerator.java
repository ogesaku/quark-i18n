package com.coditory.quark.i18n.api;

import java.util.List;

import static java.util.stream.Collectors.toList;

public interface I18nPathGenerator extends I18nKeyGenerator {
    static I18nPathGenerator strictPathGenerator() {
        return new StrictI18nPathGenerator();
    }

    static I18nPathGenerator relaxedPathGenerator() {
        return new PrefixFallbackI18nPathGenerator();
    }

    List<I18nPath> paths(List<I18nPath> prefixes, I18nPath path);

    default List<I18nKey> keys(List<I18nPath> prefixes, I18nKey key) {
        I18nPath path = key.path();
        return this.paths(prefixes, path).stream()
                .map(l -> key.withPath(path))
                .collect(toList());
    }
}

class StrictI18nPathGenerator implements I18nPathGenerator {
    @Override
    public List<I18nPath> paths(List<I18nPath> prefixes, I18nPath path) {
        return List.of(prefixes.get(0).child(path));
    }
}

class PrefixFallbackI18nPathGenerator implements I18nPathGenerator {
    @Override
    public List<I18nPath> paths(List<I18nPath> prefixes, I18nPath path) {
        return prefixes.stream()
                .map(prefix -> prefix.child(path))
                .collect(toList());
    }
}
