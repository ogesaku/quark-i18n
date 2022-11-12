package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;
import com.coditory.quark.i18n.api.I18nPath;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class NumberI18NFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "number";

    @Override
    public I18nFormatter formatter(I18nMessageTemplates messages, List<String> args) {
        if (args.size() > 1) {
            throw new RuntimeException("Expected at most one argument got: " + args);
        }
        return args.size() == 0
                ? createDefaultFormatter(messages)
                : createTypedFormatter(messages, args.get(0));
    }

    private I18nFormatter createDefaultFormatter(I18nMessageTemplates messages) {
        NumberFormat intFormat = createDefaultIntegerFormatter(messages);
        NumberFormat numberFormat = createDefaultNumberFormatter(messages);
        return (value) ->
                (value instanceof Integer || value instanceof Long || value instanceof Byte)
                        ? intFormat.format(value)
                        : numberFormat.format(value);
    }

    private I18nFormatter createTypedFormatter(I18nMessageTemplates messages, String type) {
        NumberFormat intFormat = createIntegerFormatter(messages, type);
        NumberFormat numberFormat = createFloatFormatter(messages, type);
        return (value) ->
                (value instanceof Integer || value instanceof Long || value instanceof Byte)
                        ? intFormat.format(value)
                        : numberFormat.format(value);
    }

    private NumberFormat createDefaultIntegerFormatter(I18nMessageTemplates messages) {
        return createIntegerFormatter(messages, "");
    }

    private NumberFormat createDefaultNumberFormatter(I18nMessageTemplates messages) {
        return createFloatFormatter(messages, "");
    }

    private NumberFormat createIntegerFormatter(I18nMessageTemplates messages, String type) {
        return messages.getTemplate(
                I18nPath.of("formats", FILTER, "int", type),
                I18nPath.of("formats", FILTER, "int"),
                I18nPath.of("formats", FILTER, "default"),
                I18nPath.of("formats", FILTER)
        )
                .map(format -> (NumberFormat) new DecimalFormat(format))
                .orElseGet(() -> NumberFormat.getIntegerInstance(messages.getLocale()));
    }

    private NumberFormat createFloatFormatter(I18nMessageTemplates messages, String type) {
        return messages.getTemplate(
                I18nPath.of("formats", FILTER, "float", type),
                I18nPath.of("formats", FILTER, "float"),
                I18nPath.of("formats", FILTER, "default"),
                I18nPath.of("formats", FILTER)
        )
                .map(format -> (NumberFormat) new DecimalFormat(format))
                .orElseGet(() -> NumberFormat.getNumberInstance(messages.getLocale()));
    }
}
