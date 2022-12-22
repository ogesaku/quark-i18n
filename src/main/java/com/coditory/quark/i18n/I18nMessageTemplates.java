package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class I18nMessageTemplates {
    private final Map<I18nKey, String> templates;
    private final I18nKeyGenerator keyGenerator;
    private final Locale locale;

    I18nMessageTemplates(
            Map<I18nKey, String> templates,
            I18nKeyGenerator keyGenerator,
            Locale locale
    ) {
        expectNonNull(templates, "templates");
        this.templates = Map.copyOf(templates);
        this.keyGenerator = expectNonNull(keyGenerator, "keyGenerator");
        this.locale = expectNonNull(locale, "locale");
    }

    @NotNull
    public Optional<String> getTemplate(@NotNull I18nPath... paths) {
        return Arrays.stream(paths)
                .map(this::getTemplate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @NotNull
    public Optional<String> getTemplate(@NotNull I18nPath path) {
        return keyGenerator.keys(List.of(), I18nKey.of(locale, path))
                .stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .findFirst();
    }

    @NotNull
    public Locale getLocale() {
        return locale;
    }
}
