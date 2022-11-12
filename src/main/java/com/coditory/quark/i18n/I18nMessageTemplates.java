package com.coditory.quark.i18n;

import com.coditory.quark.i18n.api.I18nKey;
import com.coditory.quark.i18n.api.I18nKeyGenerator;
import com.coditory.quark.i18n.api.I18nPath;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class I18nMessageTemplates {
    private final Map<I18nKey, String> templates;
    private final I18nKeyGenerator keyGenerator;
    private final Locale locale;

    public I18nMessageTemplates(Map<I18nKey, String> templates, I18nKeyGenerator keyGenerator, Locale locale) {
        this.templates = templates;
        this.keyGenerator = keyGenerator;
        this.locale = locale;
    }

    public Optional<String> getTemplate(I18nPath... paths) {
        return Arrays.stream(paths)
                .map(this::getTemplate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public Optional<String> getTemplate(I18nPath path) {
        return keyGenerator.keys(List.of(), I18nKey.of(locale, path))
                .stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .findFirst();
    }

    public Locale getLocale() {
        return locale;
    }
}
