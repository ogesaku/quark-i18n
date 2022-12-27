package com.coditory.quark.i18n.formatter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class FloatI18nFilter extends I18nFormatter {
    private static final String FILTER = "float";

    public FloatI18nFilter() {
        super(FILTER);
    }

    @Override
    @NotNull I18nFormatter.I18nValueFormatter createFormatterForFormat(@NotNull String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat::format;
    }

    @Override
    @NotNull I18nFormatter.I18nValueFormatter createFormatterForStyle(@NotNull Locale locale, @Nullable String style) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return numberFormat::format;
    }
}
