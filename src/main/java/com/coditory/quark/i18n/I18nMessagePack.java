package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.coditory.quark.i18n.I18nMessages.EMPTY_ARGS;

public interface I18nMessagePack {
    static I18nMessagePackBuilder builder() {
        return new I18nMessagePackBuilder();
    }

    @NotNull
    I18nMessages localized(@NotNull Locale locale);

    @NotNull
    I18nMessagePack addPrefix(@NotNull String prefix);

    @NotNull
    String getMessage(@NotNull Locale locale, @NotNull String key, Object... args);

    @NotNull
    default String getMessage(@NotNull Locale locale, @NotNull String key) {
        return getMessage(locale, key, EMPTY_ARGS);
    }

    @NotNull
    String format(@NotNull Locale locale, @NotNull String template, Object... args);
}
