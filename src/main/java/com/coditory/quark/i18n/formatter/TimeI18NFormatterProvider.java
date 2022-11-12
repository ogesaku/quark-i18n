package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;
import com.coditory.quark.i18n.api.I18nPath;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimeI18NFormatterProvider implements I18nFormatterProvider {
    public static final String FILTER = "time";

    private static final Map<String, Integer> TIME_STYLES = Map.of(
            "short", DateFormat.SHORT,
            "medium", DateFormat.MEDIUM,
            "long", DateFormat.LONG,
            "full", DateFormat.FULL
    );

    @Override
    public I18nFormatter formatter(I18nMessageTemplates messages, List<String> args) {
        if (args.size() > 1) {
            throw new RuntimeException("Expected at most one argument got: " + args);
        }
        String type = args.isEmpty() ? "" : args.get(0);
        DateFormat formatter = createFormatter(messages, type);
        return formatter::format;
    }

    private DateFormat createFormatter(I18nMessageTemplates messages, String type) {
        return messages.getTemplate(
                I18nPath.of("formats", FILTER, type),
                I18nPath.of("formats", FILTER, "default"),
                I18nPath.of("formats", FILTER)
        )
                .map(format -> (DateFormat) new SimpleDateFormat(format))
                .orElseGet(() -> createSystemFormatter(messages.getLocale(), type));
    }

    private DateFormat createSystemFormatter(Locale locale, String type) {
        int dateStyle = TIME_STYLES.getOrDefault(type, DateFormat.DEFAULT);
        return DateFormat.getTimeInstance(dateStyle, locale);
    }
}
