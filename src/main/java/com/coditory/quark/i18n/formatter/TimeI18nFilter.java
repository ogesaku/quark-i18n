package com.coditory.quark.i18n.formatter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public final class TimeI18nFilter extends I18nFormatter {
    private static final String FILTER = "time";
    private static final Map<String, Integer> DATE_STYLES = Map.of(
            "short", DateFormat.SHORT,
            "medium", DateFormat.MEDIUM,
            "long", DateFormat.LONG,
            "full", DateFormat.FULL
    );

    public TimeI18nFilter() {
        super(FILTER);
    }

    @Override
    @NotNull I18nFormatter.I18nValueFormatter createFormatterForFormat(@NotNull String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat::format;
    }

    @Override
    @NotNull I18nFormatter.I18nValueFormatter createFormatterForStyle(@NotNull Locale locale, @Nullable String style) {
        int dateStyle = DATE_STYLES.getOrDefault(style, DateFormat.DEFAULT);
        DateFormat dateFormat = DateFormat.getTimeInstance(dateStyle, locale);
        return dateFormat::format;
    }
}
