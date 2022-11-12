package com.coditory.quark.i18n.api;

import com.coditory.quark.i18n.I18nMessagePackBuilder;
import com.coditory.quark.i18n.I18nMessages;

import java.util.Locale;

public interface I18nMessagePack {
    static I18nMessagePackBuilder builder() {
        return new I18nMessagePackBuilder();
    }

    I18nMessages forLocale(Locale locale);

    default String getMessage(Locale locale, String key, Object... args) {
        return forLocale(locale)
                .getMessage(key, args);
    }

    default String getMessage(Locale locale, String key) {
        return getMessage(locale, key, new Object[0]);
    }

    default String format(Locale locale, String template, Object... args) {
        return forLocale(locale)
                .format(template, args);
    }

    default String format(Locale locale, String template) {
        return format(locale, template, new Object[0]);
    }
}
