package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class TimeI18nFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "time";

    private static final Map<String, Integer> TIME_STYLES = Map.of(
            "short", DateFormat.SHORT,
            "medium", DateFormat.MEDIUM,
            "long", DateFormat.LONG,
            "full", DateFormat.FULL
    );

    @Override
    @NotNull
    public I18nFormatter formatter(@NotNull FormatterContext context) {
        requireNonNull(context);
        String style = extractStyle(context);
        I18nMessages messages = context.getMessages();
        DateFormat formatter = createFormatter(messages, style);
        return formatter::format;
    }

    private String extractStyle(FormatterContext context) {
        if (context.getArgs().size() > 1) {
            throw new IllegalArgumentException("Formatter " + FILTER + " expects max 1 argument. Got: " + context.getArgs());
        }
        return context.getFirstArgOrNull();
    }

    private DateFormat createFormatter(I18nMessages messages, String type) {
        return Stream.of(
                        I18nPath.ofNullable("formats", FILTER, type),
                        I18nPath.of("formats", FILTER, "default"),
                        I18nPath.of("formats", FILTER)
                )
                .map(messages::getMessageOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .map(format -> (DateFormat) new SimpleDateFormat(format))
                .orElseGet(() -> createSystemFormatter(messages.getLocale(), type));
    }

    private DateFormat createSystemFormatter(Locale locale, String type) {
        int dateStyle = TIME_STYLES.getOrDefault(type, DateFormat.DEFAULT);
        return DateFormat.getTimeInstance(dateStyle, locale);
    }
}
