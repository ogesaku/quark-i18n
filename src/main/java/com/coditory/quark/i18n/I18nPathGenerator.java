package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.toList;

public interface I18nPathGenerator extends I18nKeyGenerator {
    @NotNull
    static I18nPathGenerator strictPathGenerator() {
        return new StrictI18nPathGenerator();
    }

    @NotNull
    static I18nPathGenerator relaxedPathGenerator() {
        return new PrefixFallbackI18nPathGenerator();
    }

    @NotNull
    List<I18nPath> paths(@NotNull List<I18nPath> prefixes, @NotNull I18nPath path);

    @NotNull
    default List<I18nKey> keys(@NotNull List<I18nPath> prefixes, @NotNull I18nKey key) {
        expectNonNull(prefixes, "prefixes");
        expectNonNull(key, "key");
        I18nPath path = key.path();
        return this.paths(prefixes, path).stream()
                .map(l -> key.withPath(path))
                .collect(toList());
    }
}

final class StrictI18nPathGenerator implements I18nPathGenerator {
    @Override
    @NotNull
    public List<I18nPath> paths(@NotNull List<I18nPath> prefixes, @NotNull I18nPath path) {
        expectNonNull(prefixes, "prefixes");
        expectNonNull(path, "path");
        return List.of(prefixes.get(0).child(path));
    }
}

final class PrefixFallbackI18nPathGenerator implements I18nPathGenerator {
    @Override
    @NotNull
    public List<I18nPath> paths(@NotNull List<I18nPath> prefixes, @NotNull I18nPath path) {
        expectNonNull(prefixes, "prefixes");
        expectNonNull(path, "path");
        return prefixes.stream()
                .map(prefix -> prefix.child(path))
                .collect(toList());
    }
}
