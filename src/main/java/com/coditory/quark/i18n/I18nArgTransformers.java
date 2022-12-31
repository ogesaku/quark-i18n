package com.coditory.quark.i18n;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

final class I18nArgTransformers {
    private static final I18nArgTransformer<Instant> INSTANT_TRANSFORMER = I18nArgTransformer.of(Instant.class, Date::from);
    private static final I18nArgTransformer<ZonedDateTime> ZONED_DATE_TIME_TRANSFORMER = I18nArgTransformer.of(ZonedDateTime.class, value -> {
        Instant instant = value.toInstant();
        return Date.from(instant);
    });
    private static final I18nArgTransformer<LocalDateTime> LOCAL_DATE_TIME_TRANSFORMER = I18nArgTransformer.of(LocalDateTime.class, value -> {
        Instant instant = value.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    });
    private static final I18nArgTransformer<LocalDate> LOCAL_DATE_TRANSFORMER = I18nArgTransformer.of(LocalDate.class, value -> {
        Instant instant = value.atTime(0, 0).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    });

    private static final List<I18nArgTransformer<?>> JAVA_TIME_TRANSFORMERS = List.of(
            INSTANT_TRANSFORMER,
            ZONED_DATE_TIME_TRANSFORMER,
            LOCAL_DATE_TIME_TRANSFORMER,
            LOCAL_DATE_TRANSFORMER
    );

    public static List<I18nArgTransformer<?>> javaTimeI18nArgTransformers() {
        return JAVA_TIME_TRANSFORMERS;
    }
}
