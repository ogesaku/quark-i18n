package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class DateTimeI18NFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "dateTime";

    private static final Map<String, Integer> DATE_STYLES = Map.of(
            "short", DateFormat.SHORT,
            "medium", DateFormat.MEDIUM,
            "long", DateFormat.LONG,
            "full", DateFormat.FULL
    );

    @Override
    @NotNull
    public I18nFormatter formatter(@NotNull I18nMessageTemplates messages, @NotNull List<String> args) {
        requireNonNull(messages);
        requireNonNull(args);
        if (args.size() > 1) {
            throw new RuntimeException("Expected at most one argument got: " + args);
        }
        String type = args.isEmpty() ? "" : args.get(0);
        DateFormat formatter = createFormatter(messages, type);
        return formatter::format;
    }

    private DateFormat createFormatter(I18nMessageTemplates messages, String type) {
        return messages.getTemplate(
                        I18nPath.ofNullable("formats", FILTER, type),
                        I18nPath.of("formats", FILTER, "default"),
                        I18nPath.of("formats", FILTER),
                        I18nPath.ofNullable("formats", DateI18NFormatterProvider.FILTER, type),
                        I18nPath.of("formats", DateI18NFormatterProvider.FILTER, "default"),
                        I18nPath.of("formats", DateI18NFormatterProvider.FILTER)
                )
                .map(format -> (DateFormat) new SimpleDateFormat(format))
                .orElseGet(() -> createSystemFormatter(messages.getLocale(), type));
    }

    private DateFormat createSystemFormatter(Locale locale, String type) {
        int dateStyle = DATE_STYLES.getOrDefault(type, DateFormat.DEFAULT);
        return DateFormat.getDateTimeInstance(dateStyle, DateFormat.SHORT, locale);
    }
}
