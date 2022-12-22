package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.I18nFormatter;
import com.coditory.quark.i18n.formatter.I18nFormatterProvider;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class CompositeI18nFormatter {
    private final ConcurrentMap<Locale, TypedFormatter> typedFormattersCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<NamedFormatterKey, I18nFormatter> namedFormattersCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, I18nFormatterProvider> typedFormatterProviders;
    private final Map<String, I18nFormatterProvider> namedFormatterProviders;
    private final I18nMessageTemplatesPack templatesPack;

    FormatterResolver(
            Map<String, I18nFormatterProvider> namedFormatterProviders,
            Map<Class<?>, I18nFormatterProvider> typedFormatterProviders,
            I18nMessageTemplatesPack templatesPack
    ) {
        expectNonNull(namedFormatterProviders, "namedFormatterProviders");
        expectNonNull(typedFormatterProviders, "typedFormatterProviders");
        expectNonNull(templatesPack, "templatesPack");
        this.typedFormatterProviders = Map.copyOf(typedFormatterProviders);
        this.namedFormatterProviders = Map.copyOf(namedFormatterProviders);
        this.templatesPack = templatesPack;
    }

    String formatByName(Locale locale, String name, Object value) {
        return formatByNameAndStyle(locale, name, null, value);
    }

    String formatByNameAndStyle(Locale locale, String name, String style, Object value) {
        NamedFormatterKey key = new NamedFormatterKey(locale, name, style);
        return namedFormattersCache.computeIfAbsent(key, (__) -> {
            I18nFormatterProvider provider = namedFormatterProviders.get(name);
            if (provider == null) {
                throw new IllegalStateException("Missing formatter: " + name);
            }
            provider.formatter(style);
        })
    }

    String formatByType(Locale locale, Object value) {

    }

    record NamedFormatterKey(Locale locale, String name, String style) {
    }
}
