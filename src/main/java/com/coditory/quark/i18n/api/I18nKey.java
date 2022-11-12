package com.coditory.quark.i18n.api;


import java.util.Locale;
import java.util.Objects;

public record I18nKey(Locale locale, I18nPath path) {
    public static I18nKey of(Locale locale, String path) {
        return new I18nKey(locale, I18nPath.of(path));
    }

    public static I18nKey of(Locale locale, String... path) {
        return new I18nKey(locale, I18nPath.of(path));
    }

    public static I18nKey of(Locale locale, I18nPath path) {
        return new I18nKey(locale, path);
    }

    public I18nKey withLocale(Locale locale) {
        return Objects.equals(this.locale, locale)
                ? this
                : new I18nKey(locale, path);
    }

    public I18nKey withPath(I18nPath path) {
        return Objects.equals(this.path, path)
                ? this
                : new I18nKey(locale, path);
    }

    public String getPathValue() {
        return path.getPathValue();
    }

    @Override
    public String toString() {
        return locale + ":" + path;
    }
}
