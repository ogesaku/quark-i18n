package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.Expression;
import com.coditory.quark.i18n.I18nFilterContext;
import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.I18nPath;
import com.coditory.quark.i18n.filter.I18nFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public abstract class I18nFormatter implements I18nFilter {
    private final String name;
    private final ConcurrentHashMap<CacheKey, I18nValueFormatter> cache = new ConcurrentHashMap<>();

    public I18nFormatter(String name) {
        this.name = requireNonNull(name);
    }

    @Override
    public void validate(@NotNull List<Expression> args) {
        requireNonNull(args);
        if (args.size() > 1) {
            throw new IllegalArgumentException("Expected 0-1 expression arguments. Got: " + args.size());
        }
    }

    @Override
    @NotNull
    public Object filter(@NotNull I18nFilterContext context) {
        requireNonNull(context);
        I18nMessages messages = context.messages();
        String style = extractStyle(context);
        CacheKey key = new CacheKey(messages.getLocale(), style);
        I18nValueFormatter formatter = cache.computeIfAbsent(key, (__) -> createFormatter(messages, style));
        return formatter.format(context.value());
    }

    private String extractStyle(I18nFilterContext context) {
        if (context.filterArgs().size() > 1) {
            throw new IllegalArgumentException("Expected max 1 argument. Got: " + context.filterArgs());
        }
        return Optional.ofNullable(context.resolveFirstFilterArgOrNull())
                .map(Objects::toString)
                .orElse(null);
    }

    private I18nValueFormatter createFormatter(I18nMessages messages, String style) {
        if (style != null && style.startsWith("[") && style.endsWith("]")) {
            String format = style.substring(1, style.length() - 1);
            return createFormatterForFormat(format);
        }
        Stream<I18nPath> paths = style == null || style.isBlank()
                ? Stream.of(I18nPath.of("formats", name, "default"), I18nPath.of("formats", name))
                : Stream.of(I18nPath.of("formats", name, style));
        return paths
                .map(messages::getMessageOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::createFormatterForFormat)
                .orElseGet(() -> createFormatterForStyle(messages.getLocale(), style));
    }

    @NotNull
    abstract I18nValueFormatter createFormatterForFormat(@NotNull String format);

    @NotNull
    abstract I18nValueFormatter createFormatterForStyle(@NotNull Locale locale, @Nullable String style);

    @FunctionalInterface
    public interface I18nValueFormatter {
        @NotNull String format(@NotNull Object value);
    }

    private record CacheKey(Locale locale, String style) {
    }
}
