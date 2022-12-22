package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class NumberI18nFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "number";

    @Override
    @NotNull
    public I18nFormatter formatter(@NotNull FormatterContext context) {
        requireNonNull(context);
        String style = extractStyle(context);
        I18nMessages messages = context.getMessages();
        return style == null || style.isEmpty()
                ? createDefaultFormatter(messages)
                : createTypedFormatter(messages, style);
    }

    private String extractStyle(FormatterContext context) {
        if (context.getArgs().size() > 1) {
            throw new IllegalArgumentException("Formatter " + FILTER + " expects max 1 argument. Got: " + context.getArgs());
        }
        return context.getFirstArgOrNull();
    }

    private I18nFormatter createDefaultFormatter(I18nMessages messages) {
        NumberFormat intFormat = createDefaultIntegerFormatter(messages);
        NumberFormat numberFormat = createDefaultNumberFormatter(messages);
        return (value) ->
                (value instanceof Integer || value instanceof Long || value instanceof Byte)
                        ? intFormat.format(value)
                        : numberFormat.format(value);
    }

    private I18nFormatter createTypedFormatter(I18nMessages messages, String style) {
        NumberFormat intFormat = createIntegerFormatter(messages, style);
        NumberFormat numberFormat = createFloatFormatter(messages, style);
        return (value) ->
                (value instanceof Integer || value instanceof Long || value instanceof Byte)
                        ? intFormat.format(value)
                        : numberFormat.format(value);
    }

    private NumberFormat createDefaultIntegerFormatter(I18nMessages messages) {
        return createIntegerFormatter(messages, "");
    }

    private NumberFormat createDefaultNumberFormatter(I18nMessages messages) {
        return createFloatFormatter(messages, "");
    }

    private NumberFormat createIntegerFormatter(I18nMessages messages, String style) {
        return Stream.of(
                        I18nPath.ofNullable("formats", FILTER, "int", style),
                        I18nPath.of("formats", FILTER, "int"),
                        I18nPath.of("formats", FILTER, "default"),
                        I18nPath.of("formats", FILTER)
                )
                .map(messages::getMessageOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .map(format -> (NumberFormat) new DecimalFormat(format))
                .orElseGet(() -> NumberFormat.getIntegerInstance(messages.getLocale()));
    }

    private NumberFormat createFloatFormatter(I18nMessages messages, String style) {
        return Stream.of(
                        I18nPath.ofNullable("formats", FILTER, "float", style),
                        I18nPath.of("formats", FILTER, "float"),
                        I18nPath.of("formats", FILTER, "default"),
                        I18nPath.of("formats", FILTER)
                )
                .map(messages::getMessageOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .map(format -> (NumberFormat) new DecimalFormat(format))
                .orElseGet(() -> NumberFormat.getNumberInstance(messages.getLocale()));
    }
}
