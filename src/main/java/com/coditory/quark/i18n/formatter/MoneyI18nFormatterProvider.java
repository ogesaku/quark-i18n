package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class MoneyI18nFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "money";

    @Override
    @NotNull
    public I18nFormatter formatter(@NotNull FormatterContext context) {
        requireNonNull(context);
        String style = extractStyle(context);
        I18nMessages messages = context.getMessages();
        NumberFormat formatter = createFormatter(messages, style);
        return formatter::format;
    }

    private String extractStyle(FormatterContext context) {
        if (context.getArgs().size() > 1) {
            throw new IllegalArgumentException("Formatter " + FILTER + " expects max 1 argument. Got: " + context.getArgs());
        }
        return context.getFirstArgOrNull();
    }

    private NumberFormat createFormatter(I18nMessages messages, String style) {
        return Stream.of(
                        I18nPath.ofNullable("formats", FILTER, style),
                        I18nPath.of("formats", FILTER, "default"),
                        I18nPath.of("formats", FILTER)
                )
                .map(messages::getMessageOrNull)
                .filter(Objects::nonNull)
                .findFirst()
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
