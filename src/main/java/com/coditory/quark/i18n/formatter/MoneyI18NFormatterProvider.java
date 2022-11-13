package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

public final class MoneyI18NFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "money";

    @Override
    @NotNull
    public I18nFormatter formatter(@NotNull I18nMessageTemplates messages, @NotNull List<String> args) {
        requireNonNull(messages);
        requireNonNull(args);
        if (args.size() > 1) {
            throw new RuntimeException("Expected at most one argument got: " + args);
        }
        String type = args.isEmpty() ? "" : args.get(0);
        NumberFormat formatter = createFormatter(messages, type);
        return formatter::format;
    }

    private NumberFormat createFormatter(I18nMessageTemplates messages, String type) {
        return messages.getTemplate(
                        I18nPath.ofNullable("formats", FILTER, type),
                        I18nPath.of("formats", FILTER, "default"),
                        I18nPath.of("formats", FILTER)
                )
                .map(format -> (NumberFormat) new DecimalFormat(format))
                .orElseGet(() -> createSystemFormatter(messages.getLocale()));
    }

    private NumberFormat createSystemFormatter(Locale locale) {
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMinimumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        return format;
    }
}
