package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.I18nFormatter;
import com.coditory.quark.i18n.formatter.I18nFormatterProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.Objects.requireNonNull;

final class FormatterResolver {
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

    I18nFormatter getFormatterByName(Locale locale, String name, String style) {
        expectNonNull(name, "name");
        expectNonNull(locale, "locale");
        I18nFormatterProvider provider = namedFormatterProviders.get(name);
        if (provider == null) {
            throw new RuntimeException("Formatter not found. Name: " + name);
        }
        I18nMessageTemplates templates = templatesPack.withLocale(locale);
        NamedFormatterKey key = new NamedFormatterKey(locale, name, style);
        return namedFormattersCache.computeIfAbsent(key, (__) -> provider.formatter(templates, style));
    }

    I18nFormatter getTypedFormatter(Locale locale) {
        expectNonNull(locale, "locale");
        I18nMessageTemplates templates = templatesPack.withLocale(locale);
        return typedFormattersCache.computeIfAbsent(locale, (__) -> new TypedFormatter(templates, typedFormatterProviders));
    }

    private record NamedFormatterKey(Locale locale, String name, String style) {
    }
}

final class LazyFormatter implements I18nFormatter {
    private final Function<Locale, I18nFormatter> provider;

    LazyFormatter(Function<Locale, I18nFormatter> provider) {
        this.provider = requireNonNull(provider);
    }

    @Override
    public @NotNull Object format(@NotNull Object value) {
        return provider;
    }
}

final class TypedFormatter implements I18nFormatter {
    private static final PassingFormatter PASSING_FORMATTER = new PassingFormatter();
    private final ConcurrentMap<Class<?>, List<Class<?>>> typeHierarchy = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, I18nFormatter> formattersByTypeCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<I18nFormatterProvider, I18nFormatter> formattersCache = new ConcurrentHashMap<>();
    private final I18nMessageTemplates templates;
    private final Map<Class<?>, I18nFormatterProvider> typedFormatterProviders;

    TypedFormatter(
            I18nMessageTemplates templates,
            Map<Class<?>, I18nFormatterProvider> typedFormatterProviders
    ) {
        expectNonNull(templates, "templates");
        expectNonNull(typedFormatterProviders, "typedFormatterProviders");
        this.typedFormatterProviders = Map.copyOf(typedFormatterProviders);
        this.templates = templates;
    }

    @Override
    @NotNull
    public Object format(@NotNull Object value) {
        return getFormatter(value.getClass())
                .format(value);
    }

    private I18nFormatter getFormatter(Class<?> type) {
        return formattersByTypeCache.computeIfAbsent(type, (__) ->
                getTypeHierarchy(type).stream()
                        .filter(typedFormatterProviders::containsKey)
                        .map(typedFormatterProviders::get)
                        .findFirst()
                        .map(this::getFormatter)
                        .orElse(PASSING_FORMATTER)
        );
    }

    private I18nFormatter getFormatter(I18nFormatterProvider provider) {
        return formattersCache.computeIfAbsent(provider, p -> p.formatter(templates));
    }

    private List<Class<?>> getTypeHierarchy(Class<?> type) {
        return typeHierarchy.computeIfAbsent(type, Reflections::getAllInterfacesAndClasses);
    }
}

final class PassingFormatter implements I18nFormatter {
    @Override
    @NotNull
    public Object format(@NotNull Object value) {
        return value;
    }
}